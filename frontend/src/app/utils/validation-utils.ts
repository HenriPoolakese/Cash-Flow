export class ValidationUtils {
  static checkValidity(selectedTimePeriod: string | null, fromDate: Date | null, toDate: Date | null): boolean {
    return !(selectedTimePeriod || (fromDate && toDate));
  }
}
