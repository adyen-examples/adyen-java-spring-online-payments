import {LitElement, html, css} from 'lit';
import './payment-link.js';

import '@vaadin/button';
import '@vaadin/number-field';
import '@vaadin/text-field';

export class PaymentLinksInterface extends LitElement {
    static properties = {
        links : { Object},
        amount : {Number},
        reference : {String},

    };

    static styles = css`
      :host{    
          width: 100%;
          max-width: 1140px;
        padding-right: 15px;
        padding-left: 15px;
        margin-right: auto;
        margin-left: auto;
    }

    .intro{
      margin: 5px;
    }
      
    .creationForm{
      margin: 15px;    
    }

    .linksList{
      margin: 15px;
    }
      
      .linkslistTitle{
        display: flex;
        align-items: center;
      }
      
      .createButton{
        margin-left: 10px;
      }
      
      .reloadButton{
        margin-left: auto;
      }
      
      ul{
        list-style-type: none;
        margin: 0;
        padding: 0;
      }
      
      li{
        margin-top: 15px;
        margin-bottom: 15px;
      }

      vaadin-text-field{
        margin-left: 10px;
      }
    `;

    constructor() {
        super();
        this.links = [];
        this.amount = "";
        this.reference = "";

        this._reload();
    }

    render() {
        return html`
    <h1>Adyen Pay by Link demo</h1>

    <div class="intro">
        <p>Create Payment Links using the form below, and test them out!</p>
        <p>Make sure the payment method you want to use in the payment links are enabled for your account.
            Refer to the <a href="https://docs.adyen.com/payment-methods#add-payment-methods-to-your-account">the documentation</a>
            to add missing payment methods.</p>
        <p>To learn more about payment links, check out <a href="https://www.adyen.com/pay-by-link">pay-by-link</a>.</p>
    </div>
    
    <div class="creationForm">
        <h2>Create a new link</h2>
        
        <vaadin-number-field
            label="Amount"
            .value = "${this.amount}"
            @input="${e => {this.amount = e.target.value;}}"
        >
            <div slot="suffix">€</div>
        </vaadin-number-field>

        <vaadin-text-field 
                label="Reference" 
                .value = "${this.reference}"
                @input="${e => {this.reference = e.target.value;}}"
                clear-button-visible>
        </vaadin-text-field>

        <vaadin-button class="createButton" theme="primary" design="Emphasized" @click="${this._create}">Create!</vaadin-button>
    </div>


    <div class="linksList">
        <div class="linkslistTitle">
            <h2>Created payment links</h2> 
            <vaadin-button class="reloadButton" theme="primary" @click="${this._reload}">Reload all links ↺</vaadin-button>
        </div>
        
        ${this.links.length === 0 ? 
            html`<p>No links created yet!</p>`        
            : html`<ul>
                    ${this.links.map( link => html`
                        <li>
                            <payment-link .link=${link}></payment-link>
                        </li>
                    `)}
                    </ul>`
        }
    </div>
        

    `;
    }

    _create(){
        fetch("/api/links", {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                "amount": this.amount,
                "reference": this.reference.length === 0 ? null : this.reference
            }) // body data type must match "Content-Type" header
        })
            .then(r =>{
                this.amount = "";
                this.reference = "";
                this._reload();
            })
            .catch(error => "Error: " + error);
    }

    _reload(){
        this.fetchLinks();
    }

    fetchLinks(){
        fetch("/api/links")
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error while fetching the Payment Links');
                }
                response.json()
                    .then(data => {
                        this.links = data
                    })
                    .catch((error) => {
                        console.error('Error:', error);
                    });
            })
    }

}
customElements.define('payment-links-interface', PaymentLinksInterface);
