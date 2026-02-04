
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AlbumEditComponent } from './album-edit.component';

describe('AlbumEditComponent', () => {
  let component: AlbumEditComponent;
  let fixture: ComponentFixture<AlbumEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlbumEditComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (key: string) => '1',
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AlbumEditComponent);
    component = fixture.componentInstance;
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load album on init', () => {
    vi.spyOn(component, 'loadAlbum');
    
    component.ngOnInit();
    
    expect(component.loadAlbum).toHaveBeenCalled();
  });

  it('should have album form', () => {
    expect(component.albumForm).toBeDefined();
  });

  it('should have loading property', () => {
    expect(component.loading).toBeDefined();
  });
});
