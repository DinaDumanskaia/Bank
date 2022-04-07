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
        clientId: null,
        buttonCreate: null,
        accounts: {RUB:null, EUR:null, USD:null}
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
        .then(data => this.setState({
                buttonCreate: true,
                clientId: data.id,
                accounts:{RUB:0, EUR:0, USD:0}
            }));
  }



  async handleSubmitGetClient(event) {
      event.preventDefault();
      this.setState({ buttonCreate: false });
      if (this.state.value == "") {
          alert("INPUT CLIENT ID");
        } else {
            const response = await fetch('http://localhost:8080/bank/v2/clients/' + this.state.value);
            if (response.ok) {
                const data = await response.json();
                this.setState({
                        clientId: data.id,
                        buttonCreate: true,
                        accounts:data.accounts
                    });
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
                <ClientPage
                    clientId={this.state.clientId}
                    accounts={this.state.accounts}
                    setAccounts={newAccount => this.setState({accounts:newAccount})}
                    />
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
        value2: '',
        clientId : props.clientId,
        buttonTransaction: false,
        value3 : '',
        transactionInfo: [],
        transactionAmounts: [],
        transactionDates: [],
        currency: "RUB",
        transactionsJson: null
    };

    this.handleChangeBalance = this.handleChangeBalance.bind(this);
    this.handleChangeBalance2 = this.handleChangeBalance2.bind(this);
    this.increaseMoney = this.increaseMoney.bind(this);
    this.decreaseMoney = this.decreaseMoney.bind(this);
    this.handleSelectCurrency = this.handleSelectCurrency.bind(this);

    this.handleSubmitGetTransaction = this.handleSubmitGetTransaction.bind(this);
    this.printTransactions = this.printTransactions.bind(this);
  }


    async increaseMoney(event) {
        event.preventDefault();
        if (!this.state.value1) {
            return;
        }
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ amount: this.state.value1, currency: this.state.currency })
        };

        const response = await fetch('http://localhost:8080/bank/v2/clients/' + this.props.clientId + '/transactions/', requestOptions);
        if (!response.ok) {
            await showError(response);
            return;
        }

        this.setState({ value1: '' });
        const balanceResponse = await fetch('http://localhost:8080/bank/v2/clients/' + this.props.clientId);
        const data = await balanceResponse.json();

        this.setState({ clientId: data.id, transactionAmounts: null, transactionDates: null, transactionInfo: null, transactionsJson: null});
        this.props.setAccounts(data.accounts);
    }

    async decreaseMoney(event) {
        event.preventDefault();
        if (!this.state.value2) {
            return;
        }
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ amount: this.state.value2 * (-1), currency: this.state.currency })
        };

        const response = await fetch('http://localhost:8080/bank/v2/clients/' + this.props.clientId + '/transactions/', requestOptions);
        if (!response.ok) {
            await showError(response);
            return;
        }

        this.setState({ value2: '' });
        const balanceResponse = await fetch('http://localhost:8080/bank/v2/clients/' + this.props.clientId);
        const data = await balanceResponse.json();

        this.setState({ clientId: data.id, transactionAmounts: null, transactionDates: null, transactionInfo: null, transactionsJson: null});
        this.props.setAccounts(data.accounts);
    }

    async handleChangeBalance(event) {
        this.setState({value1: event.target.value});
    }

    async handleChangeBalance2(event) {
        this.setState({value2: event.target.value});
    }

    parseTransactions(data, currency) {
        const amounts = data.map(transactionAmount => transactionAmount.amount);
        const dates = data.map(transactionDate => transactionDate.date);
        const combined = data.map(element => `${element.amount} ${currency} ${element.date}`);
        return {amounts, dates, combined};
    }

    handleSelectCurrency(event) {
        const currency = event.target.value;
        if (this.state.transactionsJson) {
            const {amounts, dates, combined} = this.parseTransactions(this.state.transactionsJson[currency], currency);
            this.setState({currency, transactionAmounts: amounts, transactionDates: dates, transactionInfo: combined});
        } else {
            this.setState({currency});
        }
    }

    async handleSubmitGetTransaction(event) {
        event.preventDefault();
        this.setState({buttonTransaction: true});
        const response = await fetch('http://localhost:8080/bank/v2/clients/' + this.props.clientId + '/transactions/');
        const dataJson = await response.json();
        const data = dataJson[this.state.currency]
        const {amounts, dates, combined} = this.parseTransactions(data, this.state.currency);

        this.setState({transactionAmounts: amounts, transactionDates: dates, transactionInfo: combined, transactionsJson: dataJson});

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
                {Object.keys(this.props.accounts)
                    .map(currency => this.props.accounts[currency] !== null
                    &&<h3>Баланс {currency}: {this.props.accounts[currency]}</h3>)} 

            </div>
            <div className="column">
                <form onSubmit={this.increaseMoney}>
                <select value={this.state.currency} onChange={this.handleSelectCurrency}>
                    <option value="RUB">RUB</option>
                    <option value="EUR">EUR</option>
                    <option value="USD">USD</option>
                </select>
                    <h3>
                    <input type="number" min="1" value={this.state.value1} onChange={this.handleChangeBalance} />
                    <input type="submit" value="Пополнить" />
                    </h3>
                </form>
                <form onSubmit={this.decreaseMoney}>
                    <h3>
                        <input type="number" min="1" value={this.state.value2} onChange={this.handleChangeBalance2} />
                        <input type="submit" value="Списать" />
                    </h3>
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
                <div>
                    <ul>
                        {this.state.transactionInfo?.map(item => {
                            return <li>{item}</li>;
                        })}
                    </ul>
                </div>


        );
    }

    render() {
        if (this.state.buttonTransaction) {
            return (
                <div>
                    <div>
                        {this.ClientInfo()}
                    </div>
                    {this.state.transactionsJson && (<div className="transactions">
                    {this.state.transactionAmounts?.length ? 'Транзакции:' : 'Транзакций нет'}
                    {this.printTransactions()}
                    </div>)}
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