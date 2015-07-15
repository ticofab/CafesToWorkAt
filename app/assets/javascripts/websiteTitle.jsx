var WebsiteTitle = React.createClass({
  render: function() {
    var text;

    if (this.props.location) {
      text = "Cafes To Work At in " + this.props.location;
    } else {
      text = "Cafes To Work At"
    }

    return (
      <div className="website-title">
        <h1>{text}</h1>
      </div>
    );
  }
});
