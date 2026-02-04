import 'zone.js';
import 'zone.js/testing';
import { getTestBed } from '@angular/core/testing';
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting,
} from '@angular/platform-browser-dynamic/testing';

// Initialize Angular testing environment
getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting(),
);

// Ensure jasmine spyOn is available globally
declare const jasmine: any;
if (typeof globalThis !== 'undefined' && typeof jasmine !== 'undefined') {
  (globalThis as any).spyOn = jasmine.spyOn.bind(jasmine);
}
