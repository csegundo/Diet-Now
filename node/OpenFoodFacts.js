const https = require('https');

module.exports = {
    getProduct(barcode, callback){
        https.get(`https://world.openfoodfacts.org/api/v0/product/${barcode}.json`, (resp) => {
            let data = '';
            resp.on('data', (chunk) => {
                data += chunk;
            });

            resp.on('end', () => {
                if(callback && typeof callback === 'function'){
                    callback(data);
                }
            });

        }).on("error", (err) => {
            console.error("Error: " + err.message);
        });
    }
};