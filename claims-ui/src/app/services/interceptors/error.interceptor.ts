import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../../core/services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((err: unknown) => {
      const e = err as HttpErrorResponse;
      let errorMessage = 'An unknown error occurred!';

      if (e.error instanceof ErrorEvent) {
        // A client-side or network error occurred.
        errorMessage = `A client-side error occurred: ${e.error.message}`;
      } else {
        // The backend returned an unsuccessful response code.
        switch (e.status) {
          case 400:
            errorMessage = 'Bad Request. Please check the data you provided.';
            break;
          case 401:
            errorMessage = 'Unauthorized. Please log in again.';
            break;
          case 403:
            errorMessage = 'Forbidden. You do not have permission to access this resource.';
            break;
          case 404:
            errorMessage = 'The requested resource was not found.';
            break;
          case 500:
            errorMessage = 'An internal server error occurred. Please try again later.';
            break;
          default:
            errorMessage = `An unexpected error occurred (Status: ${e.status}).`;
            break;
        }
      }

      notificationService.showError(errorMessage);

      console.error('API Error Handled by Interceptor', {
        url: req.url,
        status: e.status,
        message: e.message,
        errorBody: e.error,
      });

      return throwError(() => e);
    })
  );
};


