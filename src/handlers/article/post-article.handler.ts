import {APIGatewayEvent, APIGatewayProxyCallback, Context} from 'aws-lambda';

export async function handler(event: APIGatewayEvent, context: Context, callback: APIGatewayProxyCallback) {
    console.log("hello world");
    return;
}
