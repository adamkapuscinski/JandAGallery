export interface IPhoto {
    id?: number;
    category?: string;
    tag?: string;
    photographyContentType?: string;
    photography?: any;
    priority?: number;
}

export class Photo implements IPhoto {
    constructor(
        public id?: number,
        public category?: string,
        public tag?: string,
        public photographyContentType?: string,
        public photography?: any,
        public priority?: number
    ) {}
}
