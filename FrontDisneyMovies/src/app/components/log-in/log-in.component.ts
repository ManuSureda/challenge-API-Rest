import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/auth/auth.service';
import { LoginCredential } from 'src/app/models/login-credential';

@Component({
  selector: 'app-log-in',
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.css']
})
export class LogInComponent implements OnInit {

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required])
  });

  alertMessage = "";

  constructor(private authService : AuthService, private router : Router) { }

  get email() { return this.loginForm.get('email'); }
  get password() { return this.loginForm.get('password'); }

  ngOnInit(): void {

  }

  onSubmit() {
    sessionStorage.clear();

    let loginCredential = new LoginCredential();
    loginCredential.email = this.email.value;
    loginCredential.password = this.password.value;

    const promise = this.authService.login(loginCredential);

    promise
      .then(response => {
        
        if (this.authService.token) {
          let redirect = this.authService.redirectUrl ? this.router.parseUrl(this.authService.redirectUrl) : '/characters';
          
          this.router.navigateByUrl(redirect);
        } else {
          this.alertMessage = "Incorrect email or password";
        }
      })
      .catch(err => {
        console.log(err);
        if (err instanceof HttpErrorResponse) {
          this.alertMessage = "Sorry, it seems the server is down, please try again later";
        } else {
          this.alertMessage = "Sorry, something went wrong, please try again";
        }
      });

  }

}
