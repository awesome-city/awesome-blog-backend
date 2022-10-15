import {Configuration} from 'webpack';
import * as path from 'path';

const config: Configuration = {
    entry: {
        article_post: path.resolve(__dirname, './src/handlers/article/post-article.handler'),
        article_get: path.resolve(__dirname, './src/handlers/article/get-article.handler')
    },
    output: {
        filename: "[name]/app.js",
        libraryTarget: 'commonjs2',
        path: path.resolve(__dirname, 'build')
    },
    devtool: "source-map",
    resolve: {
        extensions: [".ts", ".js"]
    },
    target: "node",
    externals: process.env.NODE_ENV === "development" ? [] : [],
    mode: process.env.NODE_ENV === "development" ? "development" : "production",
    module: {
        rules: [
            {
                test: /\.ts?$/,
                loader: "ts-loader"
            }
        ]
    }
}

export default config;
