var GooglePlace = function (name, address, placeId){
    this.name = name;
    this.address = address;
    this.placeId = placeId;
};

GooglePlace.fromJson = function (json){
    return new GooglePlace(json.name, json.formatted_address, json.placeId);
};

var CafeResult = React.createClass({
  render: function() {
    return (
      <div className="cafeResult">
        <h4>{this.props.name}</h4>
        <p>{this.props.address}</p>
      </div>
    )
  }
});

var CafeResultBox = React.createClass({
  getInitialState: function() {
    return { results: [] };
  },
  handleResults: function(data) {
    // parse json results
    var places = [];
    for(var i = 0; i < data.length; i++) {
        var result = new GooglePlace.fromJson(data[i]);
        places.push(result)
    }

    // set state
    this.setState({ results: places })
  },
  componentDidMount: function() {
    // prepare url + query param
    var endpoint = "http://localhost:9000/api/place/validate?query=" + this.props.query;

    // perform http post request
    $.ajax({
          url: endpoint,
          method: 'GET',
          dataType: 'json',
          cache: false,
          success: function(data) {
            console.log(data)
            this.handleResults(data)
          }.bind(this),
          error: function(xhr, status, err) {
            console.error(postCafeUrl, status, err.toString());
          }.bind(this)
        });
  },
  render: function() {
    return (
      <div>
        { this.state.results.length > 0 ? <CafeResult name={this.state.results[0].name} address={this.state.results[0].address} /> : null }
        { this.state.results.length > 1 ? <CafeResult name={this.state.results[1].name} address={this.state.results[1].address} /> : null }
        { this.state.results.length > 2 ? <CafeResult name={this.state.results[2].name} address={this.state.results[2].address} /> : null }
      </div>
    )
  }
});

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

    // compose search query and trigger parent reaction
    var query = name + "+" + city + "+" + country;
    this.props.onSubmit(query)

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
  getInitialState: function() {
      return { query: null, submitted: false };
  },
  onSubmit: function(searchQuery) {
      this.setState({ query: searchQuery, submitted: true });
  },
  render: function() {
    return (
      <div>
        <WebsiteTitle />
        <CafeForm onSubmit={this.onSubmit} />
        {this.state.submitted ? <CafeResultBox query={this.state.query} /> : null }
      </div>
    );
  }
});

React.render(<AddCafePage />,
             document.getElementById('content'));
