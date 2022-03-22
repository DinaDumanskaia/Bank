const e = React.createElement;
name = `ВАСЯ`

const element = (
  <div>
    <h1>Здравствуйте, {name}!</h1>
    <h2>Рады вас видеть.</h2>
  </div>
);


      ReactDOM.render(
        element,
        document.getElementById('root')
      );