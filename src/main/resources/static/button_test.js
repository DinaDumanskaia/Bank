const e = React.createElement;
name = `ВАСЯ1`




class NameForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
        value: '',
        postId: null,
        clientId: null
    };

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

    componentDidMount() {
        // Simple POST request with a JSON body using fetch
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title: 'React POST Request Example' })
        };
        fetch('https://reqres.in/api/posts', requestOptions)
            .then(response => response.json())
            .then(data => this.setState({ postId: data.id }));
    }

    componentDidMount2() {
        // Simple POST request with a JSON body using fetch
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
        };
        fetch('http://localhost:8080/bank/v1/clients/', requestOptions)
            .then(response => response.json())
            .then(data => this.setState({ clientId: data.id }));
    }

  handleChange(event) {
    this.setState({value: event.target.value});
  }

  handleSubmit(event) {
    this.setState({value : this.state.postId});
    event.preventDefault();
  }



  render() {
    return (
       <div>
         <h1>Здравствуйте, {this.state.postId}!</h1>
         <h2>Рады вас видеть22. {this.state.value}</h2>
            <form onSubmit={this.handleSubmit}>
                <label>
                  Name:
                  <input type="text" value={this.state.value} onChange={this.handleChange} />
                </label>
                <input type="submit" value="Submit" />
              </form>
        </div>

    );
  }
}

const element = (
    <NameForm />
);





      ReactDOM.render(
        element,
        document.getElementById('root')
      );