import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { LoginModalService, Principal, Account } from 'app/core';

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {
    account: Account;
    modalRef: NgbModalRef;
    carouselPhotos: any[];

    constructor(private principal: Principal, private loginModalService: LoginModalService, private eventManager: JhiEventManager) {}

    ngOnInit() {
        // this.carouselPhotos = ['IMG_8976.jpg', 'IMG_20180617_130431.jpg'];
        this.carouselPhotos = [
            { src: 'IMG_8976.jpg', header: 'Sesja Narzeczeńska', paragraph: '' },
            { src: 'IMG_20180617_130431.jpg', header: 'Sesja Ślubna', paragraph: 'Dostępne wkrótce' }
        ];
        this.principal.identity().then(account => {
            this.account = account;
        });
        this.registerAuthenticationSuccess();
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', message => {
            this.principal.identity().then(account => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }
    getPhotoSourcePath(x: string) {
        const result = '../../content/images/' + x;
        console.log('result :', result);
        return result;
    }
}
