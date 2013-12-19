Products.ProductsController = Ember.ArrayController.extend({
  actions: {
    createProduct: function () {
      var brand = this.get('newBrand');
      var title = this.get('newTitle');
      var price = this.get('newPrice');

      // Create the new Product model
      var product = this.store.createRecord('product', {
        brand: brand,
        title: title,
        price: price
      });

      // Clear the "New Product" text fields
      this.set('newBrand', '');
      this.set('newTitle', '');
      this.set('newPrice', '');

      // Save the new model
      product.save();
    }
  }
});