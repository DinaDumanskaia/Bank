
const e = React.createElement;

class ClientPage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};

    this.handleChangeBalance = this.handleChangeBalance.bind(this);
    this.handleSubmitBalanceModify = this.handleSubmitBalanceModify.bind(this);

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

  ClientInfo() {
    return (
        <div>
            <br />
                <h2>Здравствуйте, {this.props.clientId}</h2>
                <h3>Ваш баланс: {this.props.balance}</h3>
            <br />
            <br />
                <form onSubmit={this.handleSubmitBalanceModify}>
                    <label>
                        Изменить баланс:
                        <input type="text" value={this.state.value1} onChange={this.handleChangeBalance} />
                    </label>
                    <input type="submit" value="Перевести" />
                </form>
            <br />
        </div>
        );
    }


render() {
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
        kek: null,
    };

    this.handleSubmitCreate = this.handleSubmitCreate.bind(this);


    this.handleSubmitGetClient = this.handleSubmitGetClient.bind(this);
    this.handleChangeId = this.handleChangeId.bind(this);
  }

  handleChangeId(event) {
   this.setState({value: event.target.value});
 }

  handleSubmitCreate(event) {
    this.setState({kek: true});
    const requestOptions = {
        method: 'POST',
    };
    fetch('http://localhost:8080/bank/v1/clients/', requestOptions)
        .then(response => response.json())
        .then(data => this.setState({ clientId: data.id, balance: data.balance }));
    event.preventDefault();
  }



  handleSubmitGetClient(event) {
      this.setState({kek: true});
      fetch('http://localhost:8080/bank/v1/clients/' + this.state.value)
            .then(response => response.json())
            .then(data => this.setState({ clientId: data.id, balance: data.balance }));
      event.preventDefault();
    }

  main() {
    return (
          <div>
           <br />
             <h1 className="hello">Добро пожаловать в <br /> Рашнбанк диджитал Кассир систем</h1>
           <br />
             <form onSubmit={this.handleSubmitCreate}>
               <input type="submit" value="Создать клиента" />
             </form>
           <br />
             <form onSubmit={this.handleSubmitGetClient}>
               <label>
                 Введите идентификатор клиента:
                   <input type="text" value={this.state.value} onChange={this.handleChangeId} />
               </label>
               <input type="submit" value="Найти клиента" />
             </form>
           </div>
        );
    }


  render() {
    if (this.state.kek) {
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