import { Observable } from 'rxjs';
import { shareReplay } from 'rxjs/operators';

type CacheKey = string;

// Utility function to memoize only the last call
export function memoizeLastReactive<T extends (...args: any[]) => Observable<any>>(
  fn: T
): (...args: Parameters<T>) => ReturnType<T> {
  let lastCacheKey: CacheKey | null = null;
  let lastObservable$: ReturnType<T> | null = null;

  return (...args: Parameters<T>) => {
    const cacheKey = JSON.stringify(args);

    // If arguments match the last cached key, reuse the observable
    if (lastCacheKey === cacheKey && lastObservable$) {
      console.debug('Cache hit:', cacheKey);
      return lastObservable$;
    }

    // Cache miss: compute a new observable
    console.debug('Cache miss or new call:', cacheKey);
    lastCacheKey = cacheKey;
    lastObservable$ = fn(...args).pipe(
      shareReplay(1) // Cache the result for all subscribers
    ) as ReturnType<T>;

    return lastObservable$;
  };
}
