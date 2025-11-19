import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavigationComponent } from './navigation.component';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('NavigationComponent', () => {
  let component: NavigationComponent;
  let fixture: ComponentFixture<NavigationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavigationComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            // Mock any properties or methods used by NavigationComponent
            params: of({}), // Mocked observable for route parameters
            snapshot: { paramMap: { get: () => 'mockValue' } } // Mock snapshot with paramMap
          }
        }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(NavigationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
