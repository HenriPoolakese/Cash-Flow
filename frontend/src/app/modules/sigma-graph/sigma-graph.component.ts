import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  NO_ERRORS_SCHEMA,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { createEdgeCurveProgram } from '@sigma/edge-curve';
import { MultiGraph } from 'graphology';
import Sigma from 'sigma';
import { createEdgeArrowProgram } from 'sigma/rendering';
import { NodeImageProgram } from "@sigma/node-image";
import { GraphData, GraphLink, GraphNode, LinkType, Tooltip } from "./graph.models";
import { Coordinates } from "sigma/types";
import { KeyValue, KeyValuePipe, NgForOf, NgIf } from "@angular/common";
import {
  applyHeavyForceAtlas,
  applyInferredSettings,
  applyMinimalForceAtlas,
  applySparseForceAtlas,
  circularNodeCoordinates,
  createLayers
} from "./node-placement.util";

@Component({
  selector: 'app-sigma-graph',
  standalone: true,
  templateUrl: './sigma-graph.component.html',
  styleUrls: ['./sigma-graph.component.scss'],
  imports: [
    NgIf,
    NgForOf,
    KeyValuePipe
  ],
  schemas: [NO_ERRORS_SCHEMA]
})
export class SigmaGraphComponent implements OnInit, OnDestroy, OnChanges {
  @ViewChild('sigmaContainer', { static: true }) container!: ElementRef;

  @Input() data?: GraphData;
  @Input() seed = 'fixedSeed105';

  @Output() onNodeClick = new EventEmitter<GraphNode>();
  @Output() onEdgeClick = new EventEmitter<GraphLink>();

  tooltip = {
    visible: false,
    x: 0,
    y: 0,
    title: '',
    content: {} as Record<string, string>,
  };


  private nodeIdToNodeMap: Map<string, GraphNode> = new Map();
  private edgeIdToLinkMap: Map<string, GraphLink> = new Map();

  private sigmaRenderer: Sigma | undefined;
  private draggedNode: string | null = null;

  private maxDragDistance = 500;

  ngOnInit(): void {
    if (this.data) {
      this.initializeGraph(this.data);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data'] && !changes['data'].firstChange) {
      // Reinitialize the graph with new data
      this.sigmaRenderer?.kill(); // Remove the previous renderer if it exists
      if (this.data) {
        this.initializeGraph(this.data);
      }
    }
  }

  keepOriginalOrder = (
    a: KeyValue<string, string>,
    b: KeyValue<string, string>
  ): number => {
    const keys = Object.keys(this.tooltip.content);
    return keys.indexOf(a.key) - keys.indexOf(b.key);
  };

  private initializeGraph(data: GraphData): void {
    this.edgeIdToLinkMap.clear();
    this.nodeIdToNodeMap.clear();
    const graph = new MultiGraph();

    const layers = createLayers(data.nodes, data.links);
    const nodeCoordinates = circularNodeCoordinates(layers);

    // Add nodes
    data.nodes.forEach(node => {
      const { x, y } = nodeCoordinates[node.id];
      let image = this.nodeImage(node);
      this.nodeIdToNodeMap.set(node.id, node);

      // Update node label to include country if it exists
      const label = node.type === "company" && node.country
        ? `${node.label} (${node.country})`
        : node.label;

      graph.addNode(node.id, {
        label: label, // Attach country to label
        size: this.nodeSize(node),
        x: x,
        y: y,
        color: this.nodeColor(node),
        image: image,
        type: image ? 'image' : undefined,
        shape: 'circle'
      });

       // Add the country sub-node if the node is external and has a country
      // if (node.type === "company" && node.country && node.isExternal) {
      //   this.addCountrySubNode(graph, node, x, y);
      // }
      // if (node.type === "private" && node.country) {
      //   this.addCountrySubNode(graph, node, x, y);
      // }


    });

    // Calculate min and max amounts
    const amounts = data.links
      .filter(link => !!link.amount)
      .map(link => link.amount) as number[];

    const minAmount = Math.min(...amounts);
    const maxAmount = Math.max(...amounts);

    // Function to normalize size between 1 and 6 based on amount
    const normalizeSize = (amount?: number | null) => {
      if (!amount) {
        return 2;
      }
      return 2 + ((amount - minAmount) / (maxAmount - minAmount)) * (4 - 1);
    };

    // Add edges with size based on normalized amount
    data.links.forEach(link => {
      const edgeId = this.createEdgeId(link);
      this.edgeIdToLinkMap.set(edgeId, link);
      graph.addEdgeWithKey(edgeId, link.source, link.target, {
        id: link.source + ' ' + link.target,
        label: this.getLinkLabel(link),
        size: normalizeSize(link.amount), // Set edge size based on normalized amount
        arrowSize: 10,
        color: this.getEdgeColor(link),
        type: ['group', 'net-flow'].includes(link.type) ? 'straight' : 'curved',
        directional: true
      });
    });

    // Apply ForceAtlas2 layout
    let hasFewEdges = data.links.length < 1500
    if(hasFewEdges) {
      applyHeavyForceAtlas(graph);
    } else {
      applyInferredSettings(graph);
    }


    // Initialize the renderer with increased arrow size
    this.sigmaRenderer = new Sigma(graph, this.container.nativeElement, {
      allowInvalidContainer: true,
      defaultEdgeType: 'curved',
      renderEdgeLabels: data.links.length < 1000, // TODO optimize this by only showing some labels
      enableEdgeEvents: true,
      autoCenter: false,
      autoRescale: true,
      minCameraRatio: 0.2,
      maxCameraRatio: 2,
      edgeProgramClasses: {
        straight: createEdgeArrowProgram({
          lengthToThicknessRatio: 5,
          widenessToThicknessRatio: 4,
        }),
        curved: createEdgeCurveProgram({
          arrowHead: {
            lengthToThicknessRatio: 5,
            widenessToThicknessRatio: 4,
          },
          curvatureAttribute: 'curvature',
          defaultCurvature: 0.25,
        }),
      },
      nodeProgramClasses: {
        image: NodeImageProgram,
      },
    });

    // Expose sigmaRenderer globally for testing
    (window as any).sigmaRenderer = this.sigmaRenderer;

    console.log('Sigma renderer initialized:', this.sigmaRenderer);

    // Add event listeners for drag-and-drop functionality
    this.sigmaRenderer.on('downNode', (e) => this.onNodeDown(e.node));
    this.sigmaRenderer.getMouseCaptor().on('mousemove', (e) => this.onMouseMove(e));
    this.sigmaRenderer.getMouseCaptor().on('mouseup', () => this.onMouseUp());
    this.sigmaRenderer.getMouseCaptor().on('wheel', () => {
      this.hideTooltip();
    });

    // Show pointer on enter
    this.sigmaRenderer.on('enterNode', (e) => {
      const node = this.nodeIdToNodeMap.get(e.node);
      if (!node?.tooltip) {
        return;
      }
      this.container.nativeElement.style.cursor = 'pointer';
    });

    this.sigmaRenderer.on('leaveNode', () => {
      this.container.nativeElement.style.cursor = 'default';
    });

    this.sigmaRenderer.on('clickNode', (e) => {
      this.hideTooltip();
      const node = this.nodeIdToNodeMap.get(e.node);
      if (!node) {
        console.warn('No matching node found for ID:', e.node);
        return;
      }


      this.onNodeClick.emit(node);

      if (!node?.tooltip) {
        console.log('No tooltip for node:', e.node);
        return;
      }

      //  alternative way to get coordinates (maybe not needed)
      /*
      const nodePosition = this.sigmaRenderer!.graphToViewport({
        x: this.sigmaRenderer!.getGraph().getNodeAttribute(e.node, 'x'),
        y: this.sigmaRenderer!.getGraph().getNodeAttribute(e.node, 'y')
      });
      */
      this.showTooltip(e.event, node.tooltip);

    });

    this.sigmaRenderer.on('enterEdge', (e) => {
      this.hideTooltip();
      const link = this.edgeIdToLinkMap.get(e.edge);
      if (!link?.tooltip) {
        return;
      }

      const node = this.nodeIdToNodeMap.get(link.source);
      this.container.nativeElement.style.cursor = 'pointer';
      const graph = this.sigmaRenderer!.getGraph();
      const edge = e.edge;


      // Temporarily increase edge size for hover
      const originalSize = graph.getEdgeAttribute(edge, 'size');
      graph.setEdgeAttribute(edge, 'originalSize', originalSize);
      graph.setEdgeAttribute(edge, 'size', 7);
      this.sigmaRenderer!.refresh();
    });

    this.sigmaRenderer.on('leaveEdge', (e) => {
      this.container.nativeElement.style.cursor = 'default';
      const graph = this.sigmaRenderer!.getGraph();
      const edge = e.edge;

      // Revert to the original edge size
      const originalSize = graph.getEdgeAttribute(edge, 'originalSize');

      if (originalSize) {
        graph.setEdgeAttribute(edge, 'size', originalSize);
        graph.removeEdgeAttribute(edge, 'originalSize');

      }

      this.sigmaRenderer!.refresh();
    });

    this.sigmaRenderer.on('clickEdge', (e) => {
      const link = this.edgeIdToLinkMap.get(e.edge);
      if (link) {
        this.onEdgeClick.emit(link);
        if (link.tooltip) {
          this.showTooltip(e.event, link.tooltip);
        }
      } else {
        console.warn('No matching link found for edge ID:', e.edge);
      }
    });

    this.maxDragDistance = 500 + this.nodeIdToNodeMap.size * 20; // Define maximum allowed distance

  }

  private createEdgeId(link: GraphLink): string {
    // If you have a specific ID in your link object, use that
    if (link.id) {
      return `edge_${link.id}`;
    }

    // Otherwise create an ID based on source, target and type
    // Add other unique identifiers if needed (like amount, timestamp, etc)
    return `edge_${link.source}_${link.target}_${link.type}`;
  }

  private nodeImage(node: GraphNode) {
    if (node.type === 'private') {

      if (!node.country || !node.isExternal) { // Internal private account
        return 'images/spy.png';
      } else { // External private account
        return `flag/${node.country.toLowerCase()}.png`;
      }
    }
    if (node.type === 'company') {
      if (!node.country || !node.isExternal) {
        return node.color ? 'images/enterprise4.png' : 'images/enterprise3.png';
      } else {
        return `flag/${node.country.toLowerCase()}.png`;
      }
    }
    if (node.type === 'account') {
      const color = node.color ?? '#green';
      return `images/wallet-${color.substring(1)}.png`;
    }
    if (node.type === 'Bank') {
      return 'images/bank.png';
    }
    return undefined;
  }

  private nodeSize(node: GraphNode) {
    if (node.type === 'account') {
      return 20;
    } else if (node.type === 'group') {
      return 20;
    } else if (node.type === 'private') {
      return 20;
    } else if (node.type === 'company') {
      return 20;
    } else if (node.type === 'Bank') {
      return 20;
    }
    return 10;
  }

  private getLinkLabel(link: GraphLink) {
    if (link.type === 'group') {
      return '';
    }
    if(link.label) {
      return link.label;
    }

    return `${link.amount} ${link.currency}`;
  }

  private nodeColor(node: GraphNode) {
    if (node.type === 'account') {
      return '#f2f5f6';
    } else if (node.type === 'company') {
      return node.color ?? '#f2f5f6';
    } else if (node.type === 'private') {
      return '#f2f5f6';
    } else if (node.type === 'Bank') {
      return '#f2f5f6';
    } else {
      return '#60cd18';
    }
  }

  private getEdgeColor(link: GraphLink): string {
    const type = link.type;
    if (type == 'credit') {
      return '#308800';
    } else if (type == 'debit') {
      return '#ae1a17';
    } else if (type == 'group') {
      return '#d1d1d1';
    } else if (type == 'net-flow') {
      return '#1f77b4';
    }
    const node = this.nodeIdToNodeMap.get(link.source);
    return node?.color ?? '#acacac';
  }

  private onNodeDown(node: string): void {
   // this.sigmaRenderer?.setSetting('autoRescale', false);
    this.draggedNode = node;
    this.sigmaRenderer!.getCamera().disable(); // Disable camera panning
  }

  private onMouseMove(e: { x: number, y: number }): void {
    if (this.draggedNode) {
      const graph = this.sigmaRenderer!.getGraph();
      const initialPos = {
        x: graph.getNodeAttribute(this.draggedNode, 'initialX') ?? graph.getNodeAttribute(this.draggedNode, 'x'),
        y: graph.getNodeAttribute(this.draggedNode, 'initialY') ?? graph.getNodeAttribute(this.draggedNode, 'y'),
      };

      const currentPos = this.sigmaRenderer!.viewportToGraph(e);

      // Calculate the distance and angle from the initial position
      const dx = currentPos.x - initialPos.x;
      const dy = currentPos.y - initialPos.y;
      const distance = Math.hypot(dx, dy);


      const maxDistance = this.maxDragDistance;
      // If within max distance, move freely; otherwise, limit to max distance
      const scalingFactor = distance > maxDistance ? maxDistance / distance : 1;

      const newX = initialPos.x + dx * scalingFactor;
      const newY = initialPos.y + dy * scalingFactor;

      graph.setNodeAttribute(this.draggedNode, 'x', newX);
      graph.setNodeAttribute(this.draggedNode, 'y', newY);

      // Save the initial position if not already stored
      if (!graph.getNodeAttribute(this.draggedNode, 'initialX')) {
        graph.setNodeAttribute(this.draggedNode, 'initialX', initialPos.x);
        graph.setNodeAttribute(this.draggedNode, 'initialY', initialPos.y);
      }

      this.sigmaRenderer!.refresh();
    }
  }


  private onMouseUp(): void {
    if (this.draggedNode) {
      this.draggedNode = null;
      this.sigmaRenderer!.getCamera().enable(); // Re-enable camera panning
    }
  }

  ngOnDestroy(): void {
    this.sigmaRenderer?.kill();
  }

  // Info tooltip
  private showTooltip(coordinates: Coordinates, tooltip: Tooltip) {
    const containerRect = this.container.nativeElement.getBoundingClientRect();
    this.tooltip = {
      visible: true,
      x: coordinates.x + containerRect.left - 50, // Adjust for tooltip offset
      y: coordinates.y + containerRect.top + 30,
      title: tooltip.title || '',
      content: tooltip.content,
    };
  }

  private hideTooltip() {
    this.tooltip.visible = false;
  }
}
