export function getContrastYIQ(hexcolor: string): string {
  // Remove '#' if present
  hexcolor = hexcolor.replace('#', '');

  // Convert to RGB
  const r = parseInt(hexcolor.substr(0, 2), 16);
  const g = parseInt(hexcolor.substr(2, 2), 16);
  const b = parseInt(hexcolor.substr(4, 2), 16);

  // Calculate YIQ
  const yiq = (r * 299 + g * 587 + b * 114) / 1000;

  // Return 'black' for light colors, 'white' for dark colors
  return yiq >= 128 ? 'black' : 'white';
}

/**
 * Returns a slightly darker color for the input. Pirmary use is node's border.
 * @param hexcolor - The original hex color (e.g., '#4CAF50' or '4CAF50').
 * @param percent - The percentage to darken the color (default is 15%).
 * @param alpha - The alpha value
 * @returns The darkened hex color.
 */
export function getDarkerColor(hexcolor: string, percent: number = 25, alpha: number = 1): string {
  // Ensure alpha is within 0 and 1
  alpha = Math.max(0, Math.min(1, alpha));

  // Remove '#' if present
  let cleanedHex = hexcolor.replace('#', '');

  // Expand 3-character hex to 6-character hex if necessary
  if (cleanedHex.length === 3) {
    cleanedHex = cleanedHex.split('').map(char => char + char).join('');
  }

  // Convert hex to RGB
  const r = parseInt(cleanedHex.slice(0, 2), 16);
  const g = parseInt(cleanedHex.slice(2, 4), 16);
  const b = parseInt(cleanedHex.slice(4, 6), 16);

  // Darken the color by reducing the RGB values by the percentage
  const darkerR = Math.max(0, Math.min(255, Math.floor(r * (1 - percent / 100))));
  const darkerG = Math.max(0, Math.min(255, Math.floor(g * (1 - percent / 100))));
  const darkerB = Math.max(0, Math.min(255, Math.floor(b * (1 - percent / 100))));

  // Return the color as rgba with the alpha value
  return `rgba(${darkerR}, ${darkerG}, ${darkerB}, ${alpha})`;
}

/**
 * Converts a number to a two-digit hexadecimal string.
 * @param c - The number to convert.
 * @returns A two-digit hexadecimal string.
 */
function toHex(c: number): string {
  const hex = c.toString(16).toUpperCase();
  return hex.length === 1 ? '0' + hex : hex;
}
