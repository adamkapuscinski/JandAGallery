import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { JandAGallerySharedModule } from 'app/shared';
import {
    PhotoComponent,
    PhotoDetailComponent,
    PhotoUpdateComponent,
    PhotoDeletePopupComponent,
    PhotoDeleteDialogComponent,
    photoRoute,
    photoPopupRoute
} from './';
import { PhotoExtendedComponent } from 'app/entities/photo/extended/photo-extended.component';

const ENTITY_STATES = [...photoRoute, ...photoPopupRoute];

@NgModule({
    imports: [JandAGallerySharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        PhotoComponent,
        PhotoDetailComponent,
        PhotoUpdateComponent,
        PhotoDeleteDialogComponent,
        PhotoDeletePopupComponent,
        PhotoExtendedComponent
    ],
    entryComponents: [PhotoComponent, PhotoUpdateComponent, PhotoDeleteDialogComponent, PhotoDeletePopupComponent, PhotoExtendedComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class JandAGalleryPhotoModule {}
