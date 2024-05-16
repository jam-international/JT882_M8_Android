package communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UploadStack
{
    private ShoppingList Owner = null;    
    private final List<UploadItem> Cue;

    public UploadStack(ShoppingList owner)
        {
            super();
            Owner = owner;
            Cue = Collections.synchronizedList(new ArrayList<UploadItem>());
        }

    public boolean add(UploadItem object)
        {
            return Cue.add(object);
        }

    public void Play()
        {
            
        while (Cue.size() > 0)
            {
                UploadItem i = Cue.get(0);
                Owner.FileUpload(i.getRemotePN(), i.getLoacalPN(), null);
                Cue.remove(0);
                
                String lpn = i.getLoacalPN().toLowerCase();
                
                
                
                MultiCmdItem mci = new MultiCmdItem(1, MultiCmdItem.dtVN, 3813, MultiCmdItem.dpNONE, Owner);
                mci.setValue(1d);
                
                if (lpn.contains("par2kmac.txt"))
                    {
                    mci.setIndex(3813);
                    Owner.WriteItem(mci);
                    }
                else if (lpn.contains("par2kax.txt"))
                    {
                        mci.setIndex(3811);
                        Owner.WriteItem(mci);
                    }
                else if (lpn.contains("par2kpid.txt"))
                    {
                        mci.setIndex(3812);
                        Owner.WriteItem(mci);
                    }
                else if (lpn.contains("par2kcop.txt"))
                    {
                        mci.setIndex(3814);
                        Owner.WriteItem(mci);
                    }
                else if (lpn.contains("tools2k.txt"))
                    {
                        mci.setIndex(3817);
                        Owner.WriteItem(mci);
                    }
            }
        }

}


