interface ErrorData {
    statusCode: number;
    code: string;
    description: string
}

export abstract class MyError extends Error {
    private readonly _statusCode: number;
    private readonly _code: string;
    private readonly _description: string;

    protected constructor(data: ErrorData, e?: string) {
        super(e);
        this.name = new.target.name;

        if (Error.captureStackTrace) {
            Error.captureStackTrace(this, this.constructor);
        }

        this._statusCode = data.statusCode;
        this._code = data.code;
        this._description = data.description;
    }

    get description(): string {
        return this._description;
    }
    get code(): string {
        return this._code;
    }
    get statusCode(): number {
        return this._statusCode;
    }
}

export class ArticleNotFoundError extends MyError {
    constructor() {
        super({
            statusCode: 404,
            code: 'E1001',
            description: 'article not found'
        });
    }
}
