import { LitElement, html, css } from 'lit';

export class DemoTitle extends LitElement {

    static styles = css`
      h2 {
        font-family: "Red Hat Mono", monospace;
        font-size: 60px;
        font-weight: 700;
        line-height: 26.4px;
        color: var(--main-highlight-text-color, rgba(237, 98, 128));
      }
      .title {
        text-align: center;
        padding: 1em;
        background: var(--main-bg-color, rgb(246, 242, 242));
      }
      .explanation {
        margin-left: auto;
        margin-right: auto;
        width: 50%;
        text-align: justify;
        font-size: 20px;
      }
    `;

    render() {
        return html`
            <div class="title">
                <h2>Miles of Smiles</h2>
                <p style="text-align:center;color:#666;font-size:14px;">Spring AI · Anthropic Claude</p>
            </div>
            <div class="explanation">
                <p>Welcome to Miles of Smiles!</p>
                <p>Click the button on the bottom right to talk to the LLM-powered customer support agent.</p>
            </div>
        `;
    }
}

customElements.define('demo-title', DemoTitle);
