package internship.may24;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyHolder> {

    Context context;
    ArrayList<MyOrderList> arrayList;
    public MyOrderAdapter(Context context, ArrayList<MyOrderList> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_order,parent,false);
        return new MyHolder(view);
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView orderId,name,address,payvia,price;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.custom_order_id);
            name = itemView.findViewById(R.id.custom_order_name);
            address = itemView.findViewById(R.id.custom_order_address);
            payvia = itemView.findViewById(R.id.custom_order_payvia);
            price = itemView.findViewById(R.id.custom_order_amount);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.orderId.setText("Order Id : "+arrayList.get(position).getOrderId());
        holder.name.setText(arrayList.get(position).getName());
        holder.price.setText(ConstantSp.PRICE_SYMBOL+arrayList.get(position).getTotalAmount());
        holder.address.setText(arrayList.get(position).getAddress()+","+arrayList.get(position).getCity()+"-"+arrayList.get(position).getPincode());

        if(arrayList.get(position).getPayVia().equalsIgnoreCase("COD")){
            holder.payvia.setText(arrayList.get(position).getPayVia());
        }
        else{
            holder.payvia.setText(arrayList.get(position).getPayVia()+" ("+arrayList.get(position).getTransactionId()+")");
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
