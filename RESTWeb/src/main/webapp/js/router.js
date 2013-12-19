Products.Router.map(function () {
  this.resource('products', { path: '/' });
});

Products.ProductsRoute = Ember.Route.extend({
  model: function () {
    return this.store.find('product');
  }
});