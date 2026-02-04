import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ArtistsCreateFacade } from './artists-create.facade';

@Component({
  selector: 'app-artists-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  providers: [ArtistsCreateFacade],
  templateUrl: './artists-create.component.html',
  styleUrl: './artists-create.component.css',
})
export class ArtistsCreateComponent {
  @Output() close = new EventEmitter<void>();

  constructor(public facade: ArtistsCreateFacade) { }

  closeModal(): void {
    this.close.emit();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.artistsForm.get('file')?.setValue(input.files[0]);
    }
  }

  get artistsForm() {
    return this.facade.artistsForm;
  }

  onSubmit(): void {
    this.facade.submit(this.close);
  }

  getFieldErrorMessage(fieldName: string): string {
    return this.facade.getFieldErrorMessage(fieldName);
  }
}
