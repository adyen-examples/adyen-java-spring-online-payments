import {LitElement, html, css} from 'lit';

import { badge } from '@vaadin/vaadin-lumo-styles/badge.js';
import '@vaadin/icons';

export class PaymentLink extends LitElement {
    static properties = {
        version: {},
        link : { Object}
    };

    static get styles(){
        return [badge,
        css`
            :host{
              background-color: #eeeeee;
            }
          
            .shell{
              display: flex;
              justify-content: space-between;
              padding: 10px;              
              border-radius: 25px;
              background-color: #eeeeee;
              align-items: center;
            }
          
          a{
          padding-left: 5px;  
          }
          
        `]
    }

    constructor() {
        super();
    }

    render() {
        return html`
            <div class="shell">
                <div><a href="${this.link.url}">${this.link.id}</a> </div>
                <div><p>${this.link.reference} </p></div>
                <div><p>${this.link.amountValue} ${this.link.amountCurrency} </p></div>
                <div><p class="expiration">Expires : ${new Date(this.link.expiresAt).toLocaleString()} </p></div>
                <div class="badge">${this._returnBadge(this.link.status)}</div>
            </div>
        `;
    }

    _returnBadge(status){
        switch(status) {
            case "active":
                return html`<span theme="badge">
                    <vaadin-icon icon="vaadin:clock" style="padding: 0.25rem"></vaadin-icon>
                    <span>${status}</span>
                </span>`;
            case "completed":
                return html`<span theme="badge success">
                    <vaadin-icon icon="vaadin:check" style="padding: 0.25rem"></vaadin-icon>
                    <span>${status}</span>
                </span>`;
            case "expired":
                return html`<span theme="badge error">
                    <vaadin-icon icon="vaadin:exclamation-circle-o" style="padding: 0.25rem"></vaadin-icon>
                    <span>${status}</span>
                </span>`;
            case "paid":
                return html`<span theme="badge success">
                    <vaadin-icon icon="vaadin:check" style="padding: 0.25rem"></vaadin-icon>
                    <span>${status}</span>
                </span>`;
            case "paymentPending":
                return html`<span theme="badge">
                    <vaadin-icon icon="vaadin:clock" style="padding: 0.25rem"></vaadin-icon>
                    <span>${status}</span>
                </span>`;
            default:
                return html`<span theme="badge contrast">
                    <vaadin-icon icon="vaadin:hand" style="padding: 0.25rem"></vaadin-icon>
                    <span>${status}</span>
                </span>`;
        }
    }

}
customElements.define('payment-link', PaymentLink);
