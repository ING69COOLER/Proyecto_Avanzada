import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService, UserRole } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRoles: UserRole[] = route.data?.['roles'] || [];

  if (!authService.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  if (expectedRoles.length === 0) {
    return true; // No roles defined to access this route
  }

  if (authService.hasRole(...expectedRoles)) {
    return true;
  }

  // Not authorized
  router.navigate(['/login']); // or redirect to an unauthorized page
  return false;
};
