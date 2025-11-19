export function getNextPage(currentPage: number, itemsPerPage: number, totalItems: number): number {
  return (currentPage * itemsPerPage) < totalItems ? currentPage + 1 : currentPage;
}

export function getPreviousPage(currentPage: number): number {
  return currentPage > 1 ? currentPage - 1 : currentPage;
}
