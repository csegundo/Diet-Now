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
    },
    getAllProductByBarcode : function(barcode = '', callback){
        if(barcode){
            openfoodfacts.getProduct(barcode, function(response){
                if(callback && typeof callback === 'function'){
                    response = JSON.parse(response);
                    // DATOS POR CADA 100g
                    let nutritionalInfo = {
                        'name' : response.product.generic_name || 'Alimento',
                        'kcal' : response.product.nutriments['energy-kcal'] || 0,
                        'grams' : 100.0,
                        'fat' : response.product.nutriments['fat_100g'] || 0,
                        'saturatedFat' : response.product.nutriments['saturated-fat_100g'] || 0,
                        'carbs' : response.product.nutriments['carbohydrates_100g'] || 0,
                        'sugar' : response.product.nutriments['sugars_100g'] || 0,
                        'proteins' : response.product.nutriments['proteins_100g'] || 0,
                        'salt' : response.product.nutriments['salt_100g'] || 0
                    };
                    callback(nutritionalInfo);
                }
            });
        }

        return { "error" : "Empty barcode" };
    }
};