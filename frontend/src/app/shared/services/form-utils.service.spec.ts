import { TestBed } from '@angular/core/testing';
import { FormUtilsService } from './form-utils.service';
import { FormControl, FormGroup, Validators, UntypedFormArray, UntypedFormControl } from '@angular/forms';

describe('FormUtilsService', () => {
  let service: FormUtilsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FormUtilsService],
    });
    service = TestBed.inject(FormUtilsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('validateAllFormFields', () => {
    it('should mark all form controls as touched', () => {
      const formGroup = new FormGroup({
        field1: new FormControl(''),
        field2: new FormControl(''),
      });

      service.validateAllFormFields(formGroup);

      expect(formGroup.get('field1')?.touched).toBe(true);
      expect(formGroup.get('field2')?.touched).toBe(true);
    });

    it('should mark nested form groups as touched', () => {
      const formGroup = new FormGroup({
        field1: new FormControl(''),
        nested: new FormGroup({
          field2: new FormControl(''),
        }),
      });

      service.validateAllFormFields(formGroup);

      expect(formGroup.get('field1')?.touched).toBe(true);
      expect(formGroup.get('nested')?.touched).toBe(true);
      expect(formGroup.get('nested.field2')?.touched).toBe(true);
    });
  });

  describe('getErrorMessageFromField', () => {
    it('should return "Field is required." for required error', () => {
      const control = new UntypedFormControl('', Validators.required);
      control.markAsTouched();
      
      const message = service.getErrorMessageFromField(control);
      
      expect(message).toBe('Field is required.');
    });

    it('should return maxlength error message', () => {
      const control = new UntypedFormControl('toolongvalue', Validators.maxLength(5));
      control.markAsTouched();
      
      const message = service.getErrorMessageFromField(control);
      
      expect(message).toBe('Field cannot be more than 5 characters long.');
    });

    it('should return minlength error message', () => {
      const control = new UntypedFormControl('ab', Validators.minLength(5));
      control.markAsTouched();
      
      const message = service.getErrorMessageFromField(control);
      
      expect(message).toBe('Field cannot be less than 5 characters long.');
    });

    it('should return empty string for valid field', () => {
      const control = new UntypedFormControl('valid');
      
      const message = service.getErrorMessageFromField(control);
      
      expect(message).toBe('');
    });
  });

  describe('getFieldErrorMessage', () => {
    it('should return error message for a field in form group', () => {
      const formGroup = new FormGroup({
        email: new FormControl('', Validators.required),
      });
      formGroup.get('email')?.markAsTouched();
      
      const message = service.getFieldErrorMessage(formGroup, 'email');
      
      expect(message).toBe('Field is required.');
    });
  });

  describe('isFormArrayRequired', () => {
    it('should return true if form array is required and touched', () => {
      const formGroup = new FormGroup({
        items: new UntypedFormArray([], Validators.required),
      });
      const field = formGroup.get('items') as UntypedFormControl;
      field.markAsTouched();
      
      const result = service.isFormArrayRequired(formGroup, 'items');
      
      expect(result).toBe(true);
    });

    it('should return false if form array is not touched', () => {
      const formGroup = new FormGroup({
        items: new UntypedFormArray([], Validators.required),
      });
      
      const result = service.isFormArrayRequired(formGroup, 'items');
      
      expect(result).toBe(false);
    });
  });
});
