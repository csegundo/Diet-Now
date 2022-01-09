"use strict"

// GENERAL
const dietnow   = require("./DietNow");
const config    = require("./config");
const http      = require("http");
const url       = require("url");
const qs        = require("querystring");

// CREACION DEL SERVIDOR
http.createServer(function(request, response){
    try {
        const { pathname, query } = url.parse(request.url);

        // Sólo aceptamos de momentos peticiones GET
        if(request.method !== 'GET'){
            response.end(`{"error": "${http.STATUS_CODES[405]}"}`);
        }
        else{
            switch(pathname){
                case '/dietnow/api/product/':
                    const { barcode } = qs.parse(query);
                    dietnow.getProductByBarcode(barcode, function(product){
                        response.end(JSON.stringify(product));
                    });
                    break;
                default:
                    response.end(`{"error": "${http.STATUS_CODES[404]}"}`);
            }
        }
    } catch (error) {
        console.error(error);
    }
}).listen(config.port);