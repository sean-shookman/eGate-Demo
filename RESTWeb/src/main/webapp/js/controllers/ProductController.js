Products.ProductController = Ember.ObjectController.extend({
  actions: {
  	removeProduct: function () {
	  var product = this.get('model');
	  product.deleteRecord();
	  product.save();
	}
  }
});