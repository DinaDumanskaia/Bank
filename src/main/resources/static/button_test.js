const e = React.createElement;

class NameForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
        value: '',
        balance: null,
        clientId: null,
        balance2: null,
        clientId2: null,
    };

    this.handleSubmitCreate = this.handleSubmitCreate.bind(this);

    this.handleSubmitGetClient = this.handleSubmitGetClient.bind(this);
    this.handleChangeId = this.handleChangeId.bind(this);

    this.handleChangeBalance = this.handleChangeBalance.bind(this);
    this.handleSubmitBalanceModify = this.handleSubmitBalanceModify.bind(this);
  }




  handleChangeId(event) {
    this.setState({value: event.target.value});
  }

  handleSubmitCreate(event) {
    const requestOptions = {
        method: 'POST',
    };
    fetch('http://localhost:8080/bank/v1/clients/', requestOptions)
        .then(response => response.json())
        .then(data => this.setState({ clientId: data.id, balance: data.balance }));
    event.preventDefault();
  }

  handleSubmitBalanceModify(event) {
      const requestOptions = {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ amount: this.state.value1 })

      };
      fetch('http://localhost:8080/bank/v1/clients/' + this.state.value + '/transactions/', requestOptions);
      event.preventDefault();
    }


  handleSubmitGetClient(event) {
            fetch('http://localhost:8080/bank/v1/clients/' + this.state.value)
            .then(response => response.json())
            .then(data => this.setState({ clientId2: data.id, balance2: data.balance }));
      event.preventDefault();
    }

  handleChangeBalance(event) {
     this.setState({value1: event.target.value});
  }


  render() {
    return (
       <div>
       <br />
         <form onSubmit={this.handleSubmitCreate}>
           <input type="submit" value="Create a new client" />
         </form>
         <form onSubmit={this.handleSubmitGetClient}>
         <label>
           Input id:
             <input type="text" value={this.state.value} onChange={this.handleChangeId} />
          </label>
           <input type="submit" value="Get client" />
         </form>
         <br />
         <h1>Здравствуйте, {this.state.clientId}</h1>
         <h2>Ваш баланс: {this.state.balance}</h2>

        <form onSubmit={this.handleSubmitBalanceModify}>
            <label>
              Change balance:
              <input type="text" value={this.state.value1} onChange={this.handleChangeBalance} />
            </label>
            <input type="submit" value="GO" />
          </form>

         <h1>{this.state.clientId2}</h1>
         <h2>{this.state.balance2}</h2>
        </div>

    );
  }
}




ReactDOM.render(
    <NameForm />,
    document.getElementById('root')
);