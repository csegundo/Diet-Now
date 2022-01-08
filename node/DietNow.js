"use strict"

const OFF = require('./OpenFoodFacts');

module.exports = {
    getProductByBarcode : function(barcode = ''){
        if(barcode){
            let openfoodfacts = new OFF();
            let product = {}, response = openfoodfacts.getProduct(barcode);
            console.log('response', response);
            return response;
        }

        return { "error" : "Empty barcode" };
    }
};