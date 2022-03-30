const e = React.createElement;

async function showError(response) {
    const errorMessage = await response.text();
    alert(`>>> ${response.status}: ${errorMessage}`);
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
    event.preventDefault();
    this.setState({buttonCreate: false, clientId: null, balance: null});
    const requestOptions = {
        method: 'POST',
    };
    fetch('http://localhost:8080/bank/v1/clients/', requestOptions)
        .then(response => response.json())
        .then(data => this.setState({ buttonCreate: true, clientId: data.id, balance: data.balance }));
  }



  async handleSubmitGetClient(event) {
      event.preventDefault();
      this.setState({ buttonCreate: false });
      if (this.state.value == "") {
          alert("INPUT CLIENT ID");
        } else {
            const response = await fetch('http://localhost:8080/bank/v1/clients/' + this.state.value);
            if (response.ok) {
                const data = await response.json();
                this.setState({ clientId: data.id, balance: data.balance, buttonCreate: true });
                return;
            }
            await showError(response);
      }

   }

  main() {
    return (
        <div>
            <div className="header">
                <h1>РашнБанк</h1>
                <h5>ДИДЖИТАЛ КАССИР СИСТЕМ</h5>
            </div>
            <div className="header">
                <form onSubmit={this.handleSubmitCreate}>
                    <input type="submit" value="Создать клиента" />
                </form>
                <br />
                <form onSubmit={this.handleSubmitGetClient}>
                    <label style={{borderRadius: "15px"}}>
                    <input type="submit" value="Найти клиента" />
                    <br />
                    ИД:
                    <input type="text" value={this.state.value} onChange={this.handleChangeId} />
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


class ClientPage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
        value1: '',
        clientId : props.clientId,
        balance : props.balance,
        buttonTransaction: false,
        value3 : '',
        transactions: []
        datestamps: []
    };

    this.handleChangeBalance = this.handleChangeBalance.bind(this);
    this.handleSubmitBalanceModify = this.handleSubmitBalanceModify.bind(this);

    this.handleSubmitGetTransaction = this.handleSubmitGetTransaction.bind(this);
    this.printTransactions = this.printTransactions.bind(this);
  }


    async handleSubmitBalanceModify(event) {
        event.preventDefault();
        if (!this.state.value1) {
            return;
        }
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ amount: this.state.value1 })
        };

        const response = await fetch('http://localhost:8080/bank/v1/clients/' + this.props.clientId + '/transactions/', requestOptions);
        if (!response.ok) {
            await showError(response);
            return;
        }

        this.setState({ value1: '' });
        const balanceResponse = await fetch('http://localhost:8080/bank/v1/clients/' + this.props.clientId);
        const data = await balanceResponse.json();

        this.setState({ clientId: data.id, balance: data.balance });
    }

    handleChangeBalance(event) {
        this.setState({value1: event.target.value});
    }

//    async handleSubmitGetTransaction(event) {
//        event.preventDefault();
//        this.setState({buttonTransaction: true});
//        const response = await fetch('http://localhost:8080/bank/v1/clients/' + this.props.clientId + '/transactions/');
//        const data = await response.json();
//        const transactions = data.map(transaction => transaction.amount));
//        this.setState({amount: data.amount, date: data.date});
//    }

    handleSubmitGetTransaction(event) {
        event.preventDefault();
        this.setState({buttonTransaction: true});
        fetch('http://localhost:8080/bank/v1/clients/' + this.props.clientId + '/transactions/')
            .then(res => res.json())
//            .then(resTransactions => this.setState({transactions : resTransactions}));
//            .then(resTransactions => console.log(resTransactions));
            .then(res => res.map(transaction => transaction.amount))
            .then(transactionAmounts => this.setState({transactions : transactionAmounts}))
//            .then(transactionDates => this.setState({datestamps : transactionDates}));
    }

    handleChangeId(event) {
        this.setState({value3: event.target.value});
    }



ClientInfo() {
    return (
        <div className="row">
            <div className="column">
                <h2>Клиент</h2>
                <h3>ИД: {this.state.clientId}</h3>
                <h3>Баланс: {this.state.balance}</h3>
            </div>
            <div className="column">
                <form onSubmit={this.handleSubmitBalanceModify}>
                    <h3>
                        Изменить баланс:
                        <input type="number" min="-2000000000" max="2000000000" value={this.state.value1} onChange={this.handleChangeBalance} />
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
                    <div className="transactions">
                    {this.state.transactions.length ? 'Транзакции:' : 'Транзакций нет'}
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


ReactDOM.render(
    <div>
        <MainPage />
    </div>,
    document.getElementById('root')
);