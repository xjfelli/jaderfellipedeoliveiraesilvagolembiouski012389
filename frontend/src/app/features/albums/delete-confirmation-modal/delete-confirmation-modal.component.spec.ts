
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { DeleteConfirmationModalComponent } from './delete-confirmation-modal.component';

describe('DeleteConfirmationModalComponent', () => {
  let component: DeleteConfirmationModalComponent;
  let fixture: ComponentFixture<DeleteConfirmationModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteConfirmationModalComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteConfirmationModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit confirm event', () => {
    const confirmSpy = vi.spyOn(component.confirm, 'emit');
    
    component.onConfirm();
    
    expect(confirmSpy).toHaveBeenCalled();
  });

  it('should emit cancel event', () => {
    const cancelSpy = vi.spyOn(component.cancel, 'emit');
    
    component.onCancel();
    
    expect(cancelSpy).toHaveBeenCalled();
  });

  it('should have title input', () => {
    component.title = 'Test Album';
    expect(component.title).toBe('Test Album');
  });

  it('should have message input', () => {
    component.message = 'Test message';
    expect(component.message).toBe('Test message');
  });
});
