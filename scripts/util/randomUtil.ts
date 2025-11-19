
// Functions to generate different random data
export function getRandomInt(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min) + min);
}

// Function to generate a random integer with a specific number of digits
export function getRandomIntWithLength(length: number): number {
    const min = Math.pow(10, length - 1);
    const max = Math.pow(10, length) - 1;
    return getRandomInt(min, max);
}

// Function to convert the random integer to a string with a given length
export function getRandomNumericString(length: number): string {
    return getRandomIntWithLength(length).toString();
}

export function getRandomInt10(): number {
    return getRandomIntWithLength(10);
}

export function getRandomInt15(): number {
    return getRandomIntWithLength(15);
}

export function getRandomFromList<T>(list: T[]): T {
    return list[Math.floor(Math.random() * list.length)];
}

export function getWeightedRandomFromList<T>(items: { item: T; weight: number }[]): T {
    // Calculate the total weight
    const totalWeight = items.reduce((accum, current) => accum + current.weight, 0);

    // Generate a random number between 0 and total weight
    const randomWeight = Math.random() * totalWeight;

    // Find and return the item that corresponds to this weight
    let cumulativeWeight = 0;
    for (const item of items) {
        cumulativeWeight += item.weight;
        if (randomWeight < cumulativeWeight) {
            return item.item;
        }
    }

    // Fallback (in case something went wrong, though this should never happen)
    return items[items.length - 1].item;
}

export function getRandomDate(start =  new Date(2020, 0, 1), end = new Date() ): string {
    const date = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()));
    return date.toISOString().split('T')[0];
}

export function getRandomTime(): string {
    const hour = getRandomInt(0, 23).toString().padStart(2, '0');
    const minute = getRandomInt(0, 59).toString().padStart(2, '0');
    const second = getRandomInt(0, 59).toString().padStart(2, '0');
    return `${hour}:${minute}:${second}`;
}