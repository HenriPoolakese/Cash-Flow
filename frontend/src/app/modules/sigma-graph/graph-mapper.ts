import {
  AccountLink,
  AccountNode, Amounts, CounterPartyAccountsDTO,
  ExternalNode,
  ExternalTransfersDTO,
  GroupLink,
  InternalTransfersDTO,
  NetFlowLink,
  OwnerAccountLink,
  OwnerNode,
  TransactionLink
} from "../../services/transaction/transaction-dto.model";
import { GraphData, GraphLink, GraphNode } from "./graph.models";
import { FilterData } from "../filter/filter.types";
import { formatAmount } from "../../utils/currency.utils";

const nodeColors = [
  '#005fac',
  '#35723d',
  '#a63e2a',
  '#78a3d5',
  '#6b4b7f',
  '#e3a125',
  '#c04831',
  '#7caf6f',
  '#148a90',
  '#c9a66b',
  '#3571A6',
  '#8a8d8f',
];


/**
 * Deals with mapping and filtering transfers to the format accepted by the sigma graphs
 */
export const mapExternalTransfers = (
  transfers: ExternalTransfersDTO,
  filterData: FilterData
): GraphData => {
  const {
    selectedCurrencies,
    selectedFlow = 'Incoming/Outgoing',
    selectedAmountPresentation,
    selectedCountries,
    selectedViewType,
  } = filterData;

  let nodes = transfers.nodes
    .map(mapExternalNode)
    .filter(node =>
      (
        (!node.currency || selectedCurrencies.includes(node.currency)) && // Apply currency filtering
        (
          (node.type !== 'company' && node.type !== 'private') || // Always include non-company and non-private nodes
          (selectedViewType !== "country") || // Include all nodes if viewType is not "country"
          ((node.type === 'company' || node.type === 'private') && node.country && selectedCountries.includes(node.country)) // Apply country filtering for company and private nodes
        )
      )
    )
    .sort((a, b) => a.id.localeCompare(b.id));

  // Collect allowed node IDs for filtering links
  const nodeIds = new Set(nodes.map(node => String(node.id)));

  // Map and filter links based on selected flow and currency
  let links: GraphLink[] = [
    ...transfers.groupLinks.map(mapGroupLink), // Group links (no currency filter needed)
    ...(selectedFlow.includes('Incoming') ?
      transfers.creditLinks.map(link => mapExternalLink(link, 'credit', selectedAmountPresentation))
        .filter(link => selectedCurrencies.includes(link.currency!)) : []),
    ...(selectedFlow.includes('Outgoing') ?
      transfers.debitLinks.map(link => mapExternalLink(link, 'debit', selectedAmountPresentation))
        .filter(link => selectedCurrencies.includes(link.currency!)) : []),
    ...(selectedFlow.includes('Netflow') ?
      transfers.netflowLinks.map(link => mapExternalLink(link, 'net-flow', selectedAmountPresentation))
        .filter(link => selectedCurrencies.includes(link.currency!)) : [])
  ];

  links = links.filter(link => nodeIds.has(String(link.source)) && nodeIds.has(String(link.target)));
  const linkedNodeIds = new Set(links.flatMap(link => [String(link.source), String(link.target)]));
  nodes = nodes.filter(node => linkedNodeIds.has(String(node.id)));

  const { filteredNodes, filteredLinks } = filterAccountNodesAndLinks(nodes, links);
  return { nodes: filteredNodes, links: filteredLinks };
};

/**
 * Deals with mapping and filtering transfers to the format accepted by the sigma graphs
 */
export const mapCounterpartyTransfers = (
  transfers: CounterPartyAccountsDTO,
  filterData: FilterData
): GraphData => {
  const {
    selectedCurrencies,
    selectedFlow = 'Incoming/Outgoing',
    selectedAmountPresentation,
    selectedCountries,
    selectedViewType,
  } = filterData;

  // Map and filter nodes
  let nodes = transfers.nodes
    .map(mapExternalNode)
    .filter(node =>
      (
        (!node.currency || selectedCurrencies.includes(node.currency)) &&
        (
          (node.type !== 'company' && node.type !== 'private') ||
          (selectedViewType !== "country") ||
          ((node.type === 'company' || node.type === 'private') && node.country && selectedCountries.includes(node.country))
        )
      )
    )
    .sort((a, b) => a.id.localeCompare(b.id));

  // Collect allowed node IDs for filtering links
  const nodeIds = new Set(nodes.map(node => String(node.id)));

  // Map and filter links based on selected flow and currency
  let links: GraphLink[] = [
    ...transfers.groupLinks.map(mapGroupLink),
    ...transfers.bankLinks.map(mapGroupLink),
    ...(selectedFlow.includes('Incoming') ?
      transfers.creditLinks.map(link => mapExternalLink(link, 'credit', selectedAmountPresentation))
        .filter(link => selectedCurrencies.includes(link.currency!)) : []),
    ...(selectedFlow.includes('Outgoing') ?
      transfers.debitLinks.map(link => mapExternalLink(link, 'debit', selectedAmountPresentation))
        .filter(link => selectedCurrencies.includes(link.currency!)) : []),
    ...(selectedFlow.includes('Netflow') ?
      transfers.netflowLinks.map(link => mapExternalLink(link, 'net-flow', selectedAmountPresentation))
        .filter(link => selectedCurrencies.includes(link.currency!)) : [])
  ];

  links = links.filter(link => nodeIds.has(String(link.source)) && nodeIds.has(String(link.target)));
  const linkedNodeIds = new Set(links.flatMap(link => [String(link.source), String(link.target)]));
  nodes = nodes.filter(node => linkedNodeIds.has(String(node.id)));

  const { filteredNodes, filteredLinks } = filterAccountNodesAndLinks(nodes, links);
  return { nodes: filteredNodes, links: filteredLinks };
};

function filterAccountNodesAndLinks(
  nodes: GraphNode[],
  links: GraphLink[]
): { filteredNodes: GraphNode[]; filteredLinks: GraphLink[] } {
  const accountNodes = nodes.filter(node => node.type === 'account');
  const companyNodes = nodes.filter(node => node.type === 'company' || node.type === 'private');
  const companyNodeIds = new Set(companyNodes.map(node => String(node.id)));

  const validAccountIds = new Set();
  const validCompanyIds = new Set();

  links.forEach(link => {
    if (companyNodeIds.has(String(link.source)) && accountNodes.some(node => node.id === link.target)) {
      validAccountIds.add(String(link.target));
      validCompanyIds.add(String(link.source));
    }
    if (companyNodeIds.has(String(link.target)) && accountNodes.some(node => node.id === link.source)) {
      validAccountIds.add(String(link.source));
      validCompanyIds.add(String(link.target));
    }
  });

  const filteredAccountNodes = accountNodes.filter(node => validAccountIds.has(String(node.id)));
  const filteredCompanyNodes = companyNodes.filter(node => validCompanyIds.has(String(node.id)));

  const filteredNodes = nodes.filter(
    node => node.type !== 'account' && node.type !== 'company' && node.type !== 'private'
  ).concat(filteredAccountNodes, filteredCompanyNodes);

  const finalNodeIds = new Set(filteredNodes.map(node => String(node.id)));
  const filteredLinks = links.filter(
    link => finalNodeIds.has(String(link.source)) && finalNodeIds.has(String(link.target))
  );

  const linkedNodeIds = new Set(filteredLinks.flatMap(link => [String(link.source), String(link.target)]));
  const nodesWithoutAloneNodes = filteredNodes.filter(node => linkedNodeIds.has(String(node.id)));

  return { filteredNodes: nodesWithoutAloneNodes, filteredLinks };
}

// FIXME temporary helper for removing duplicates
function removeDuplicates(graphLinks: GraphLink[]): GraphLink[] {
  const links: Map<string, GraphLink> = new Map();
  for (const link of graphLinks) {
    links.set(`${link.source}_${link.target}_${link.type}`, link);
  }
  return [...links.values()];
}

export const mapInternalTransfers = (transfers: InternalTransfersDTO, filterData: FilterData) => {
  const {
    selectedFlow = 'Incoming/Outgoing',
    selectedCurrencies,
    selectedAmountPresentation
  } = filterData;

  let nodes = [
    ...transfers.ownerNodes.map(mapOwnerNode),
    ...transfers.accountNodes
      .filter((node) => (!node.currency || selectedCurrencies.includes(node.currency)))
      .map(mapAccountNode)
  ]

  let ownerAccountLinks = transfers.ownerAndAccountLink.map(mapOwnerAccountLink);
  let links: GraphLink[] = [...ownerAccountLinks];

  if (selectedFlow.includes('Netflow')) {
    links = links.concat(transfers.netFlowLinks.map((link) => mapInternalNetFlowLink(link, selectedAmountPresentation)));
  } else {
    links = links.concat(transfers.accountLinks.map((link) => mapAccountLink(link, selectedAmountPresentation)))
  }

  return { nodes, links }
}

// External Transfer Nodes

function mapExternalNode(node: ExternalNode): GraphNode {
  return {
    ...node,
    id: String(node.id),
    label: node.type === 'account' ? node.currency! : node.name,
    country: node.country,
    currency: node.currency,
    tooltip: node.type === 'company' && node.country && node.iban
      ? {
        title: node.name,
        content: {
          ...(node.iban ? { 'IBAN': node.iban } : {}),
        },
      }
      : node.type === 'account'
        ? {
          title: node.name,
          content: {
            ...(node.iban ? { 'Account': node.iban } : {}),
            ...(node.country ? { 'Country': node.country } : {}),
          },
        }
        : undefined,
    isExternal: true,
    color: node.type === 'account' ? getNodeColor(node.name): undefined,
  };
}

function hashString(str: string): number {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    const char = str.charCodeAt(i);
    hash = (hash * 31 + char) & 0xffffffff;
  }
  return Math.abs(hash);
}

function getNodeColor(nodeName: string): string {
  const hash = hashString(nodeName);
  return nodeColors[hash % nodeColors.length];
}


// External Tranfer Links

function selectExternalAmount(amounts: Amounts, amountPresentation: string) {
  switch (amountPresentation) {
    case 'thousands':
      return amounts?.thousands;
    case 'millions':
      return amounts?.millions;
    case 'full':
    default:
      return amounts?.original;
  }
}

function mapExternalLinkAmount(link: TransactionLink, amountPresentation: string) {
  let amount = selectExternalAmount(link.amounts, amountPresentation);
  const formattedAmount = formatAmount(amount, amountPresentation);
  return `${formattedAmount} ${link.currency}`;
}

function mapExternalLink(
  link: TransactionLink,
  type: 'net-flow' | 'debit' | 'credit',
  presentation: string) {
  let content: Record<string, string> = {
    'Average amount': formatAmount(
      selectExternalAmount(link.avgAmounts, presentation)
      , presentation) + ' ' + link.currency,
  };

  if (link.medianAmounts?.original) {
    content['Median amount'] = formatAmount(
      selectExternalAmount(link.medianAmounts, presentation),
      presentation) + ' ' + link.currency;
  }

  if (link.count) {
    content['Number of transactions'] = link.count.toString();
  }

  if (link.earliestDate) {
    content['Earliest transaction date'] = link.earliestDate;
  }

  if (link.latestDate) {
    content['Latest transaction date'] = link.latestDate;
  }

  let amount = selectExternalAmount(link.amounts, presentation);

  return {
    ...link,
    amount: amount,
    label: mapExternalLinkAmount(link, presentation),
    type,
    tooltip: {
      content: content
    }
  };
}


function mapGroupLink(link: GroupLink): GraphLink {
  return { ...link, type: 'group' };
}


// Internal Transfer Nodes

function mapOwnerNode(node: OwnerNode): GraphNode {
  return {
    ...node,
    id: node.customer_id,
    type: 'company',
    label: node.customer_name,
    country: node.country,
    isExternal: false,
    color: getNodeColor(node.customer_name + node.customer_id),
  }
}

function mapAccountNode(node: AccountNode, idx: number): GraphNode {
  return {
    ...node,
    id: node.customer_iban,
    type: 'account',
    label: node.currency,
    country: node.country,
    currency: node.currency,
    tooltip: {
      title: node.customer_name,
      content: {
        'Account': node.customer_iban,
        'Country': node.country,
      }
    },
    color: getNodeColor(node.customer_name + node.customer_id),
  }
}

// Internal Tranfer Links
function selectInternalAmount(link: AccountLink | NetFlowLink, amountPresentation: string) {
  switch (amountPresentation) {
    case 'thousands':
      return link.amountK;
    case 'millions':
      return link.amountM;
    case 'full':
    default:
      return (link as NetFlowLink)?.netFlow ?? (link as AccountLink)?.amount;
  }
}

function selectInternalAverage(link: AccountLink | NetFlowLink, amountPresentation: string) {
  switch (amountPresentation) {
    case 'thousands':
      return link.averageK;
    case 'millions':
      return link.averageM;
    case 'full':
    default:
      return link.average;
  }
}

function selectInternalMedian(link: AccountLink | NetFlowLink, amountPresentation: string) {
  switch (amountPresentation) {
    case 'thousands':
      return link.medianK;
    case 'millions':
      return link.medianM;
    case 'full':
    default:
      return link.median;
  }
}


function mapOwnerAccountLink(link: OwnerAccountLink): GraphLink {
  return { ...link, label: link.target, type: 'group' };
}

function internalLinkLabel(link: AccountLink | NetFlowLink, amountPresentation: string) {
  let amount = selectInternalAmount(link, amountPresentation);
  const formattedAmount = formatAmount(amount, amountPresentation);
  return `${formattedAmount} ${link.currency}`;
}

function mapAccountLink(link: AccountLink, presentation: string): GraphLink {
  let content: Record<string, string> = {
    'Average amount': formatAmount(selectInternalAverage(link, presentation), presentation) + ' ' + link.currency,
  };

  if (link.median) {
    content['Median amount'] = formatAmount(
      selectInternalMedian(link, presentation),
      presentation) + ' ' + link.currency;
  }

  content = {
    ...content,
    'Number of transactions': link.transactionCount.toString(),
    'Earliest transaction date': link.earliestDate,
    'Latest transaction date': link.latestDate
  }

  return {
    ...link,
    label: internalLinkLabel(link, presentation),
    type: 'account',
    tooltip: {
      content: content
    }
  };
}

function mapInternalNetFlowLink(link: NetFlowLink, amountPresentation: string): GraphLink {
  return {
    source: link.sourceAccountId,
    target: link.targetAccountId,
    label: internalLinkLabel(link, amountPresentation),
    amount: link.netFlow,
    currency: link.currency,
    type: 'net-flow'
  };
}

