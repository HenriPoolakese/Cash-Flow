import { Component, Input, Output, EventEmitter, OnChanges } from '@angular/core';
import { CurrencyPipe, NgClass, NgForOf, NgIf } from "@angular/common";
import { CdkDrag } from "@angular/cdk/drag-drop";

@Component({
  selector: 'app-flow-table',
  standalone: true,
  imports: [
    CurrencyPipe,
    NgForOf,
    NgClass,
    CdkDrag,
    NgIf
  ],
  templateUrl: './flow-table.component.html',
  styleUrls: ['./flow-table.component.scss']
})
export class FlowTableComponent implements OnChanges {
  @Input() data: any[] = [];
  @Input() flowDirection: string = 'to';
  @Output() close = new EventEmitter<void>();

  sortColumns: { column: string; direction: 'asc' | 'desc' | 'default' }[] = [
    { column: 'currency', direction: 'asc' },
    { column: 'amount', direction: 'desc' }
  ];

  originalData: any[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 10;
  paginatedData: any[] = [];

  ngOnChanges(): void {
    this.originalData = [...this.data];
    this.updatePagination();
  }

  onClose(): void {
    this.close.emit();
  }

  onSort(column: string): void {
    const existingSort = this.sortColumns.find(sort => sort.column === column);

    if (existingSort) {
      existingSort.direction =
        existingSort.direction === 'asc' ? 'desc' :
          existingSort.direction === 'desc' ? 'default' :
            'asc';

      if (existingSort.direction === 'default') {
        this.sortColumns = this.sortColumns.filter(sort => sort.column !== column);
      }
    } else {
      this.sortColumns.push({ column, direction: 'asc' });
    }

    if (this.sortColumns.length > 0) {
      this.sortData();
    } else {
      this.data = [...this.originalData];
    }

    this.updatePagination();
  }

  private sortData(): void {
    this.data.sort((a, b) => {
      for (const sort of this.sortColumns) {
        const comparison = this.compareValues(a, b, sort.column, sort.direction);
        if (comparison !== 0) {
          return comparison;
        }
      }
      return 0;
    });
  }

  private compareValues(a: any, b: any, column: string, direction: 'asc' | 'desc' | 'default'): number {
    if (direction === 'default') return 0;

    let valueA = a[column];
    let valueB = b[column];

    switch (column) {
      case 'amount':
      case 'averageAmount':
      case 'frequency':
        valueA = parseFloat(valueA) || 0;
        valueB = parseFloat(valueB) || 0;
        break;

      case 'participantName':
      case 'currency':
      case 'counterparty':
        valueA = valueA?.toString().toLowerCase() || '';
        valueB = valueB?.toString().toLowerCase() || '';
        break;

      case 'earliestDate':
      case 'latestDate':
        valueA = new Date(valueA).getTime() || 0;
        valueB = new Date(valueB).getTime() || 0;
        break;

      default:
        valueA = valueA?.toString() || '';
        valueB = valueB?.toString() || '';
    }

    if (valueA < valueB) {
      return direction === 'asc' ? -1 : 1;
    } else if (valueA > valueB) {
      return direction === 'asc' ? 1 : -1;
    } else {
      return 0;
    }
  }

  shouldShowCounterpartyAccount(): boolean {
    return (
      this.paginatedData.some(row => row.counterpartyAccount)
    );
  }


  getSortIcon(column: string): string {
    const sort = this.sortColumns.find(sort => sort.column === column);
    if (!sort) return '▲▼';

    return sort.direction === 'asc' ? '▲' : '▼';
  }

  updatePagination(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedData = this.data.slice(startIndex, endIndex);
  }

  setPage(page: number): void {
    if (page > 0 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
    }
  }

  get totalPages(): number {
    return Math.ceil(this.data.length / this.itemsPerPage);
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }
}
