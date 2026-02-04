import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArtistsCreateComponent } from './artists-create.component';

describe('ArtistsCreateComponent', () => {
  let component: ArtistsCreateComponent;
  let fixture: ComponentFixture<ArtistsCreateComponent>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArtistsCreateComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ArtistsCreateComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
