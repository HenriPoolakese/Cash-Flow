import { GraphNode, GraphLink } from "./graph.models";
import { MultiGraph } from "graphology";
import forceAtlas2 from "graphology-layout-forceatlas2";

export function createLayers(nodes: GraphNode[], links: GraphLink[]): {
  groupLayer: GraphNode[];
  accountLayer: GraphNode[];
  companyLayer: GraphNode[];
  bankLayer: GraphNode[];
} {
  const groupLayer: GraphNode[] = [];
  const accountLayer: GraphNode[] = [];
  const companyLayer: GraphNode[] = [];
  const bankLayer: GraphNode[] = [];


  // Helper function to determine connected nodes
  const getConnectedNodes = (nodeId: string) => {
    return links
      .filter(link => link.source === nodeId || link.target === nodeId)
      .map(link => (link.source === nodeId ? link.target : link.source));
  };

  nodes.forEach(node => {
    if (node.type === 'group') {
      groupLayer.push(node);
    } else if (node.type === 'account') {
      accountLayer.push(node);
    } else if (['company', 'private'].includes(node.type!)) {
      companyLayer.push(node);
    } else if (node.type === 'Bank') {
      bankLayer.push(node);
    }
  });

  // Sort nodes within each layer to minimize crossings using barycenter heuristic
  const sortLayer = (layer: GraphNode[], otherLayers: GraphNode[][]) => {
    return layer.sort((a, b) => {
      const aConnections = getConnectedNodes(a.id).filter(id =>
        otherLayers.some(layer => layer.some(node => node.id === id))
      );
      const bConnections = getConnectedNodes(b.id).filter(id =>
        otherLayers.some(layer => layer.some(node => node.id === id))
      );

      const aBarycenter = aConnections.reduce((sum, id) => {
        const index = otherLayers.flat().findIndex(node => node.id === id);
        return sum + (index >= 0 ? index : 0);
      }, 0) / (aConnections.length || 1);

      const bBarycenter = bConnections.reduce((sum, id) => {
        const index = otherLayers.flat().findIndex(node => node.id === id);
        return sum + (index >= 0 ? index : 0);
      }, 0) / (bConnections.length || 1);

      return aBarycenter - bBarycenter;
    });
  };

  const hasFewNodes = nodes.length < 100;

  // Sort each layer to minimize crossings
  const sortedGroupLayer = hasFewNodes ? sortLayer(groupLayer, [accountLayer, companyLayer]) : groupLayer;
  const sortedAccountLayer = hasFewNodes ? sortLayer(accountLayer, [groupLayer, companyLayer]) : accountLayer;
  const sortedCompanyLayer = hasFewNodes ? sortLayer(companyLayer, [groupLayer, accountLayer]) : companyLayer;
  const sortedBankLayer = hasFewNodes ? sortLayer(bankLayer, [groupLayer, accountLayer, companyLayer]) : bankLayer;


  return {
    groupLayer: sortedGroupLayer,
    accountLayer: sortedAccountLayer,
    companyLayer: sortedCompanyLayer,
    bankLayer
  };
}

export function circularNodeCoordinates(
  layers: {
    groupLayer: GraphNode[];
    accountLayer: GraphNode[];
    companyLayer: GraphNode[];
    bankLayer: GraphNode[];
  },
): Record<string, { x: number; y: number }> {

  const coordinates: Record<string, { x: number; y: number }> = {};

  // Set fixed position for group nodes (center)
  layers.groupLayer.forEach((node, index) => {
    coordinates[node.id] = { x: 0, y: 0 };
  });

  // Set circular positions for account nodes (middle layer)
  const accountRadius = 200;
  layers.accountLayer.forEach((node, index) => {
    const angle = (index / layers.accountLayer.length) * 2 * Math.PI;
    coordinates[node.id] = {
      x: accountRadius * Math.cos(angle),
      y: accountRadius * Math.sin(angle),
    };
  });

  // Set circular positions for company/private nodes (outer layer)
  const companyRadius = 400;
  layers.companyLayer.forEach((node, index) => {
    const angle = (index / layers.companyLayer.length) * 2 * Math.PI;
    coordinates[node.id] = {
      x: companyRadius * Math.cos(angle),
      y: companyRadius * Math.sin(angle),
    };
  });

  // Set circular positions for bank nodes (outer layer)
  const bankRadius = 600;
  layers.bankLayer.forEach((node, index) => {
    const angle = (index / layers.bankLayer.length) * 2 * Math.PI;
    coordinates[node.id] = {
      x: bankRadius * Math.cos(angle),
      y: bankRadius * Math.sin(angle),
    };
  });

  return coordinates;
}


export function applyHeavyForceAtlas(graph: MultiGraph) {
  forceAtlas2.assign(graph, {
    iterations: 100,
    settings: {
      linLogMode: false,
      gravity: 1.5,
      strongGravityMode: true,
      scalingRatio: 800,
      slowDown: 3,
      barnesHutOptimize: true,
      barnesHutTheta: 0.5,
      adjustSizes: false,
    }
  });
}

export function applyInferredSettings(graph: MultiGraph) {
  const settings = forceAtlas2.inferSettings(graph);
  settings.outboundAttractionDistribution = false;
  settings.linLogMode = true;
  settings.strongGravityMode = false;
  settings.barnesHutOptimize = true;
  settings.barnesHutTheta =  2.8;
  forceAtlas2.assign(graph, { iterations: 100, settings });
}

export function applyMinimalForceAtlas(graph: MultiGraph) {
  forceAtlas2.assign(graph, {
    iterations: 10,
    settings: {
      linLogMode: false,
      gravity: 3,
      strongGravityMode: true,
      scalingRatio: 10,
      slowDown: 4.4,
      barnesHutOptimize: false,
      barnesHutTheta: 0.5,
      adjustSizes: false,
    }
  });
}

export function applySparseForceAtlas(graph: MultiGraph) {
  forceAtlas2.assign(graph, {
    iterations: 100,
    settings: {
      outboundAttractionDistribution: true,
      linLogMode: true, // Use linear mode for large graphs
      gravity: 2, // Lower gravity to reduce central pull
      strongGravityMode: false,
      scalingRatio: 5000, // Increase scaling to space nodes further apart
      slowDown: 10, // Adjust for stability; higher values reduce erratic movements
      barnesHutOptimize: true, // Optimize for large graphs to improve performance
      barnesHutTheta: 2.5,
      adjustSizes: false, // Enable to prevent overlap based on node sizes
    }
  });
}
