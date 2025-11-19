import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlowTableComponent } from './flow-table.component';

describe('FlowTableComponent', () => {
  let component: FlowTableComponent;
  let fixture: ComponentFixture<FlowTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlowTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlowTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
