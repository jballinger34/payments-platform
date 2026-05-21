import {useEffect, useState } from "react";
import axios from "axios";
import './App.css';

const API_BASE = "http://localhost:8080/payments";

function App() {
	const [payments, setPayments] = useState([]);
	const [loading, setLoading] = useState(true);
	const[form, setForm] = useState(
		{ payerName: "", payeeName: "", amount: "", currency: "GBP" }
	);
	
	const formatApiError = (data) => {
		if (!data) return "Unknown error occurred";
		if (typeof data === "string") return data;

		const lines = [];

		const title = data.error || data.message || "Unknown error";

		lines.push(title);

		if (data.message && data.message !== data.error) {
			lines.push(data.message);
		}

		if (data.status) {
			lines.push(`Status: ${data.status}`);
		}

		if (data.timestamp) {
			const date = new Date(data.timestamp);
			if (!isNaN(date.getTime())) {
				lines.push(`Time: ${date.toLocaleString()}`);
			}
		}
		return lines.join("\n");
	};
	//fetch payments from backend
	const fetchPayments = async () => {
		try { 
			setLoading(true);
			const res = await axios.get(API_BASE);
			setPayments(res.data);
		} catch (err) {
			if(err.response) {
				alert(formatApiError(err.response.data));
			}
		} finally {
			setLoading(false);
		}
	};
	//run once page loads
	useEffect(() => { fetchPayments(); }, []);
	
	//handle input changes
	const handleChange = (e) => {
		setForm({...form, [e.target.name]: e.target.value, });
	};

	//sumbit new payment
	const handleSubmit = async (e) => {
		e.preventDefault();
		try{
			await axios.post(API_BASE, {
				...form,
				amount: parseFloat(form.amount),
			});
			//reset after success
			setForm({ payerName: "", payeeName: "", amount: "", currency: "GBP" });
			fetchPayments();
		} catch (err) {
			if(err.response){
				alert(formatApiError(err.response.data));
			}
		}
	};
	const updateStatus = async(id, newStatus) => {
		try { 
			await axios.put(`${API_BASE}/${id}/status`, {
				status: newStatus,
			});
			fetchPayments();
		} catch (err) {
			if(err.response) {
				alert(formatApiError(err.response.data));
			}
		}
	};
	return (
		<div className = "container">
			<h1>Payment Dashboard</h1>
			
			<div className = "card">
				<h2>Create Payment</h2>
				
				<form onSubmit={handleSubmit} className = "form">
					<input name="payerName" placeholder = "Payer Name" value = {form.payerName} onChange = {handleChange}/>
					<input name="payeeName" placeholder = "Payee Name" value = {form.payeeName} onChange = {handleChange}/>
					<input name="amount" placeholder = "Amount" type = "number" step="0.01" min="0.01" value = {form.amount} onChange = {handleChange}/>
					<select name="currency"  value = {form.currency} onChange = {handleChange}>
						<option value = "GBP">GBP</option>
						<option value = "USD">USD</option>
					</select>
					<button type="submit">Create Payment</button>
				</form>
			</div>
			<div classname="card">
				<h2>Payments</h2>
				{loading ? (<p>Loading...</p>) : payments.length === 0 ? (<p> No payments found </p> ) : (
					<table>
						<thead>
							<tr>
								<th>ID</th>
								<th>Payer</th>
								<th>Payee</th>
								<th>Amount</th>
								<th>Currency</th>
								<th>Status</th>
							</tr>
						</thead>
						<tbody>
							{payments.map((p) => (
								<tr key={p.id}>
                  							<td>{p.id}</td>
                 							<td>{p.payerName}</td>
                 							<td>{p.payeeName}</td>
                  							<td>{p.amount}</td>
							                <td>{p.currency}</td>
                  							<td>
										<select value={p.status} onChange={(e) => updateStatus(p.id, e.target.value)}>
											<option value="INITIATED">INITIATED</option>
											<option value="PROCESSING">PROCESSING</option>
											<option value="COMPLETED">COMPLETED</option>
											<option value="FAILED">FAILED</option>
										</select>
									</td>
                						</tr>
              						))}
						</tbody>
					</table>

				)}
			</div>
		</div>
	);
}

export default App;
