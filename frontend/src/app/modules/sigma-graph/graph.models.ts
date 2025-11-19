export interface GraphData {
  nodes: GraphNode[];
  links: GraphLink[];
}

export interface GraphNode {
  id: string;
  type?: string;
  label: string;
  country?: string | null;
  currency?: string | null;
  tooltip?: Tooltip;
  isExternal?: boolean;
  color?: string;
}

// TODO add dashed links and net support
export type LinkType = 'debit' | 'credit' |'group' | 'net-flow' | 'account' | 'bank';

export interface GraphLink {
  id?: string;
  source: string;
  target: string;
  label: string;
  type: LinkType;
  amount?: number | null;
  amounts?: {
    original: number,
    millions: number,
    thousands: number
  }
  currency?: string | null;
  tooltip?: Tooltip;
}

export interface Tooltip {
  title?: string;
  content: Record<string, string>
}
