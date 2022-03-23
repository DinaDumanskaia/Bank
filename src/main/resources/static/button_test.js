
const e = React.createElement;

function AAAAA(props) {
  return <h1>Hello, {props.uuid}</h1>;
}

class NameForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
        value: '',
        balance: null,
        clientId: null,
        balance2: null,
        clientId2: null,
        kek: null,
        uuid: '123'
    };

    this.handleSubmitCreate = this.handleSubmitCreate.bind(this);

    this.handleSubmitKEK = this.handleSubmitKEK.bind(this);

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

  handleSubmitKEK(event) {
//    this.setState({kek: true});
    this.setState({uuid: '456'});
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

  lol() {
    return (
          <div>
           <br />
             <h1 className="hello">Добро пожаловать в <br /> Рашнбанк диджитал Кассир систем</h1>
           <br />
             <form onSubmit={this.handleSubmitCreate}>
               <input type="submit" value="Создать клиента" />
             </form>
           <br />
           <br />
             <form onSubmit={this.handleSubmitKEK}>
               <input type="submit" value="KEK" />
             </form>
           <br />
             <h2>Здравствуйте, {this.state.clientId}</h2>
             <h3>Ваш баланс: {this.state.balance}</h3>
           <br />
             <form onSubmit={this.handleSubmitGetClient}>
               <label>
                 Введите идентификатор клиента:
                   <input type="text" value={this.state.value} onChange={this.handleChangeId} />
               </label>
               <input type="submit" value="Найти клиента" />
             </form>
           <br />
             <h2>Клиент : {this.state.clientId2}</h2>
             <h3>Баланс : {this.state.balance2}</h3>
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

    kek() {
        return <AAAAA uuid={this.state.uuid} />;
    }


  render() {
    if (this.state.kek) {
        return this.kek();
    }
    return (
    <div>
        {this.kek()}
        {this.lol()}
    </div>
    );

  }
}




ReactDOM.render(
    <div>
        <NameForm />
    </div>,
    document.getElementById('root')
);