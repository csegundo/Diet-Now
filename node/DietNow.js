"use strict"

const openfoodfacts = require('./OpenFoodFacts');

module.exports = {
    getProductByBarcode : function(barcode = '', callback){
        if(barcode){
            openfoodfacts.getProduct(barcode, function(response){
                if(callback && typeof callback === 'function'){
                    response = JSON.parse(response);
                    // DATOS POR CADA 100g
                    let product = {
                        'name' : response.product.generic_name || 'Product',
                        'active' : true,
                        'kcal' : response.product.nutriments['energy-kcal'] || 0,
                        'grams' : 100.0
                    };
                    callback(product);
                }
            });
        }

        return { "error" : "Empty barcode" };
    }
};