import {APIGatewayEvent, APIGatewayProxyCallback, APIGatewayProxyResult, Context} from 'aws-lambda';
import middy from '@middy/core';
import {baseMiddleware} from '../../common/middleware/base.middleware';

async function baseHandler(event: APIGatewayEvent, context: Context, callback: APIGatewayProxyCallback): Promise<APIGatewayProxyResult> {
    return Promise.resolve({
        statusCode: 200,
        body: JSON.stringify({a: 1})
    });
}

export const handler = middy(baseHandler).use(baseMiddleware)
