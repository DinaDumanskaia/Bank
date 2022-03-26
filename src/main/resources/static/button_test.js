
const e = React.createElement;




class ClientPage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
        clientId : props.clientId,
        balance : props.balance,
        buttonTransaction: false,
        value3 : '',
        transactions: []
    };

    this.handleChangeBalance = this.handleChangeBalance.bind(this);
    this.handleSubmitBalanceModify = this.handleSubmitBalanceModify.bind(this);

    this.handleSubmitGetTransaction = this.handleSubmitGetTransaction.bind(this);
    this.printTransactions = this.printTransactions.bind(this);
  }


    componentDidUpdate() {
        fetch('http://localhost:8080/bank/v1/clients/' + this.props.clientId)
            .then(response => response.json())
            .then(data => this.setState({ clientId: data.id, balance: data.balance }));
    }


    handleSubmitBalanceModify(event) {
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ amount: this.state.value1 })
        };
        fetch('http://localhost:8080/bank/v1/clients/' + this.props.clientId + '/transactions/', requestOptions);
        event.preventDefault();
    }

    handleChangeBalance(event) {
        this.setState({value1: event.target.value});
    }



    handleSubmitGetTransaction(event) {
        this.setState({buttonTransaction: true});
        fetch('http://localhost:8080/bank/v1/clients/' + this.props.clientId + '/transactions/')
            .then(res => res.json())
            .then(res => res.map(transaction => transaction.amount))
            .then(transactionAmounts => this.setState({transactions : transactionAmounts}));
    }

    handleChangeId(event) {
        this.setState({value3: event.target.value});
    }



ClientInfo() {
    return (
        <div class="row">
            <div class="column">
                <h2>Клиент</h2>
                <h3>ИД: {this.state.clientId}</h3>
                <h3>Баланс: {this.state.balance}</h3>
            </div>
            <div class="column">
                <form onSubmit={this.handleSubmitBalanceModify}>
                    <h3>
                        Изменить баланс:
                        <input type="text" value={this.state.value1} onChange={this.handleChangeBalance} />
                    </h3>
                    <input type="submit" value="Перевести" />
                </form>
                    <form onSubmit={this.handleSubmitGetTransaction}>
                    <h3>
                        Запросить выписку:
                    </h3>
                    <input type="submit" value="Получить"/>
                </form>
            </div>
        </div>
    );
}


    printTransactions() {
        return (
            <ul>
                {this.state.transactions.map(item => {
                    return <li>{item}</li>;
                })}
            </ul>
        );
    }

    render() {
        if (this.state.buttonTransaction) {
            return (
                <div>
                    <div>
                        {this.ClientInfo()}
                    </div>
                    <div class="transactions">
                        Транзакции:
                        {this.printTransactions()}
                    </div>
                </div>
            );
        }

        return (
            <div>
                {this.ClientInfo()}
            </div>
        );
    }
}

class MainPage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
        value: '',
        balance: null,
        clientId: null,
        buttonCreate: null,
    };

    this.handleSubmitCreate = this.handleSubmitCreate.bind(this);
    this.handleSubmitGetClient = this.handleSubmitGetClient.bind(this);
    this.handleChangeId = this.handleChangeId.bind(this);
  }

  handleChangeId(event) {
   this.setState({value: event.target.value});
 }

  handleSubmitCreate(event) {
    this.setState({buttonCreate: true});
    const requestOptions = {
        method: 'POST',
    };
    fetch('http://localhost:8080/bank/v1/clients/', requestOptions)
        .then(response => response.json())
        .then(data => this.setState({ clientId: data.id, balance: data.balance }));
    event.preventDefault();
  }



  handleSubmitGetClient(event) {
      this.setState({buttonCreate: true});
      fetch('http://localhost:8080/bank/v1/clients/' + this.state.value)
            .then(response => response.json())
            .then(data => this.setState({ clientId: data.id, balance: data.balance }));
      event.preventDefault();
   }

  main() {
    return (
        <div>
            <div class="header">
                <h1>РашнБанк</h1>
                <h5>ДИДЖИТАЛ КАССИР СИСТЕМ</h5>
            </div>
            <div class="header">
                <form onSubmit={this.handleSubmitCreate}>
                    <input type="submit" value="Создать клиента" />
                </form>
                <br />
                <form onSubmit={this.handleSubmitGetClient}>
                    <label style={{borderRadius: "15px"}}>
                    <br />
                    ИД:
                    <input type="text" value={this.state.value} onChange={this.handleChangeId} />
                    <input type="submit" value="Найти клиента" />
                    </label>
                </form>
            </div>
        </div>
        );
    }


  render() {
    if (this.state.buttonCreate) {
        return (
            <div>
                {this.main()}
                <ClientPage clientId={this.state.clientId} balance={this.state.balance} />
            </div>
        );
    }
    return (
        <div>
            {this.main()}
        </div>
    );

  }
}


ReactDOM.render(
    <div>
        <MainPage />
    </div>,
    document.getElementById('root')
);