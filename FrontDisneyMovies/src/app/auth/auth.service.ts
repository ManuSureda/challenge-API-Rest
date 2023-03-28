import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { LoginCredential } from '../models/login-credential';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  token = undefined;
  redirectUrl = undefined;
  loginUrl = "http://localhost:8080/auth/login";

  constructor(private http : HttpClient, private router : Router) { }

  login(loginCredential : LoginCredential): Promise<any> {

    // console.log("entre en el metodo login de Auth.Service.ts");    

    const promise = this.http.post(this.loginUrl, loginCredential, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }), 
      observe: 'response' //tendre que borrar esto?
    })
    .toPromise();

    promise
      .then(response => {
        // Verificar si la respuesta es una instancia de HttpResponse
        if (response instanceof HttpResponse) {

          // Buscar el header de autorización en los headers
          const authorizationHeader = response.headers.get('authorization');
          if (authorizationHeader) {
            this.token = authorizationHeader;
            sessionStorage.setItem('token', this.token);
          } else {
            console.error('No se encontró el header de Authorization en la respuesta.');
          }
        }
      })
      .catch(err => {        
        console.log(err);
      });
  
    return promise;
  }
  
}
