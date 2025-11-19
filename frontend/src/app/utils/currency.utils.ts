export function formatCurrency(amount: number, currency: string = 'EUR', digits = 0): string {
  return new Intl.NumberFormat('en', {
    style: 'currency',
    currency: currency,
    minimumFractionDigits: digits,
    maximumFractionDigits: digits,
  }).format(amount)
    .replaceAll(',', ' ');
}

export function formatDecimal(amount: number, digits = 0): string {
  return new Intl.NumberFormat('en', {
    style: 'decimal',
    minimumFractionDigits: digits,
    maximumFractionDigits: digits,
  }).format(amount)
    .replaceAll(',', ' ');
}


export function formatAmount(amount: number, amountPresentation: string) {
  switch (amountPresentation) {
    case 'thousands':
      return formatDecimal(amount, 2) + 'K';
    case 'millions':
      return formatDecimal(amount, 2) + 'M';
    case 'full':
    default:
      return formatDecimal(amount);
  }
}
