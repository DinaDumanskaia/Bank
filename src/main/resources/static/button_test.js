
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
        <div>
          <div style={{background: "#8ED9B6", borderRadius: "15px", float: "right", width: "500px", padding: "10px", boxSizing: "border-box", textAlign: "center" }}>
            <br />
                <h2>Клиент</h2>
                <h3>ИД: {this.state.clientId}</h3>
                <h3>Ваш баланс: {this.state.balance}</h3>
            <br />
            </div>
            <br />
                <form onSubmit={this.handleSubmitBalanceModify}>
                    <label>
                        Изменить баланс:
                        <input type="text" value={this.state.value1} onChange={this.handleChangeBalance} />
                    </label>
                    <input type="submit" value="Перевести" />
                </form>
            <br />
                <form onSubmit={this.handleSubmitGetTransaction}>
                <label>
                    Запросить выписку:
                </label>
                    <input type="submit" value="Получить"/>
                </form>
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

       printTransactions2() {
            return ('Hello');
        }

render() {


    if (this.state.buttonTransaction) {
    return (
    <div>

    {this.printTransactions()}
    {this.ClientInfo()}

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
           <br />
             <h1 className="hello" style={{borderRadius: "15px", float: "none", width: "1200px", padding: "10px", boxSizing: "border-box", position: "relative", textAlign: "center" }}>
                        РашнБанк Диджитал Кассир Систем</h1>
           <br />
           <div style={{background: "#FF9282", borderRadius: "15px", float: "left", width: "500px", padding: "10px", boxSizing: "border-box", textAlign: "center" }}>
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
                <ClientPage clientId={this.state.clientId} balance={this.state.balance} />
                {this.main()}
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