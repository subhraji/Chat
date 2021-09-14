import com.example.chatapp.model.repo.add_group_members.AddGroupMemberRepository
import com.example.chatapp.model.repo.add_group_members.AddGroupMemberRepositoryImpl
import com.example.chatapp.model.repo.chat_image.ChatImageUploadRepository
import com.example.chatapp.model.repo.chat_image.ChatImageUploadRepositoryImpl
import com.example.chatapp.model.repo.create_group.CreateGroupRepository
import com.example.chatapp.model.repo.create_group.CreateGroupRepositoryImpl
import com.example.chatapp.model.repo.get_profile.GetProfileRepository
import com.example.chatapp.model.repo.get_profile.GetProfileRepositoryImpl
import com.example.chatapp.model.repo.login.LoginRepository
import org.koin.androidx.compose.get
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import com.example.chatapp.model.repo.login.LoginRepositoryImpl
import com.example.chatapp.model.repo.sync_contacts.SyncContactsRepository
import com.example.chatapp.model.repo.sync_contacts.SyncContactsRepositoryImpl
import com.example.chatapp.model.repo.update_profile.UpdateProfileRepository
import com.example.chatapp.model.repo.update_profile.UpdateProfileRepositoryImpl
import com.example.chatapp.model.repo.verify_otp.VerifyOtpRepository
import com.example.chatapp.model.repo.verify_otp.VerifyOtpRepositoryImpl
import com.example.chatapp.viewmodel.*

val appModule = module {

    single<LoginRepository> { LoginRepositoryImpl(get()) }
    viewModel { LoginViewModel(get()) }

    single<VerifyOtpRepository> { VerifyOtpRepositoryImpl(get()) }
    viewModel { VerifyOtpViewModel(get()) }

    single<SyncContactsRepository> { SyncContactsRepositoryImpl(get()) }
    viewModel { SyncContactsViewModel(get()) }

    viewModel { FriendChatViewModel(get()) }

    viewModel { GroupChatViewModel(get()) }

    viewModel { ChatUserViewModel(get()) }

    single<ChatImageUploadRepository> { ChatImageUploadRepositoryImpl(get()) }
    viewModel { UploadChatImageViewModel(get()) }

    single<CreateGroupRepository> { CreateGroupRepositoryImpl(get()) }
    viewModel { CreateGroupViewModel(get(), get()) }

    single<GetProfileRepository> { GetProfileRepositoryImpl(get()) }
    viewModel { GetProfileViewModel(get()) }

    single<UpdateProfileRepository> { UpdateProfileRepositoryImpl(get()) }
    viewModel { UpdateProfileViewModel(get()) }

    single<AddGroupMemberRepository> { AddGroupMemberRepositoryImpl(get()) }
    viewModel { AddGroupMembersViewModel(get()) }

}