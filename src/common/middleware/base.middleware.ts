import httpEventNormalizer from '@middy/http-event-normalizer';
import httpHeaderNormalizer from '@middy/http-header-normalizer';
import urlEncodePathParser from '@middy/http-urlencode-path-parser';
import httpSecurityHeaders from '@middy/http-security-headers';
import httpCors from '@middy/http-cors';
import httpErrorHandler from '@middy/http-error-handler';
import {injectLambdaContext, Logger} from '@aws-lambda-powertools/logger';

const logger = new Logger();

export const baseMiddleware = [
    httpEventNormalizer(),
    httpHeaderNormalizer(),
    urlEncodePathParser(),
    httpSecurityHeaders(),
    httpCors({
        origins: ['*.4mo.blog'],
        methods: 'GET, POST, PUT, DELETE, OPTIONS',
        headers: 'Content-Type'
    }),
    httpErrorHandler({
        logger: logger.error
    }),
    injectLambdaContext(logger, {logEvent: true})
]
