var CafeForm = React.createClass({
  handleSubmit: function(e) {
    e.preventDefault();

    // get values from input fields
    var name = React.findDOMNode(this.refs.name).value.trim();
    var city = React.findDOMNode(this.refs.city).value.trim();
    var country = React.findDOMNode(this.refs.country).value.trim();

    // checks on the fields
    if (!name || !city || !country) {
      return;
    }

    // prepare url + query param
    var endpoint = "http://localhost:9000/api/cafe/validate?query=" + name + "+" + city + "+" + country;

    // perform http post request
    $.ajax({
          url: endpoint,
          method: 'GET',
          dataType: 'json',
          cache: false,
          success: function(data) {
            console.log(data)
          }.bind(this),
          error: function(xhr, status, err) {
            console.error(postCafeUrl, status, err.toString());
          }.bind(this)
        });

    // clears the form fields
    React.findDOMNode(this.refs.name).value = '';
    React.findDOMNode(this.refs.city).value = '';
    React.findDOMNode(this.refs.country).value = '';

    return;
  },
  render: function() {
    return (
      <form className="cafeForm" onSubmit={this.handleSubmit}>
        <input type="text" placeholder="Café name" ref="name" />
        <input type="text" placeholder="City" ref="city" />
        <input type="text" placeholder="Country" ref="country" />
        <input type="submit" value="Post" />
      </form>
    );
  }
});

var AddCafePage = React.createClass({
  render: function() {
    return (
      <div>
        <WebsiteTitle />
        <CafeForm />
      </div>
    );
  }
});

React.render(<AddCafePage />,
             document.getElementById('content'));
