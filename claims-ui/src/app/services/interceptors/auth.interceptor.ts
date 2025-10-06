import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Use httpOnly cookies for authentication; ensure cookies are sent with every request
  const authReq = req.clone({ withCredentials: true });
  return next(authReq);
};
